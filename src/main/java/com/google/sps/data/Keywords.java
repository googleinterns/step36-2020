package com.google.sps.data;

import java.io.IOException;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * Provide methods for the storage, extraction, and retrieval of keywords.
 * TODO: Change to more object-oriented design (Abstract class or Interface)
 */
public final class Keywords {

  private static final int MAX_NUM_KEYWORDS = 10;

  // We don't want any terms that are far too un-salient to include in the keywords list.
  // 0.2 is a good minimum threshold as, especially when extracting saliency from a list of
  // label, many of the high-relevancy terms end up only getting assigned a salience of 0.08.
  private static final double MIN_SALIENCE_THRESHOLD = 0.08;

  // For labels, we want high-confidence labels. Many of the labels below 0.60 seem to end
  // up being only tangetially related to the contents of the picture.
  private static final double MIN_SCORE_THRESHOLD = 0.60;

  /**
   * @return a list of the 10 most salient keywords from the datastore in accordance to the keys.
   */
  public static Collection<String> getKeywords(String keyString) {
    UserService userService = UserServiceFactory.getUserService();
    Map<String, Collection<String>> allKeywords = getKeyToKeywordMap(userService.getCurrentUser().getUserId());        
    return allKeywords.get(keyString);
  }

  /**
   * @return a key-to-keyword mapping of all keywords that the current user has created.
   */
  public static Map<String, Collection<String>> getAllKeywords() {
    UserService userService = UserServiceFactory.getUserService();
    return getKeyToKeywordMap(userService.getCurrentUser().getUserId());
  }

  /**
   * Adds the labels from the analysis of an image into the datastore.
   * @return the key pointing to the newly added datastore entity.
   */
  public static String addKeywords(List<EntityAnnotation> blobAnalysis) throws IOException {
    List<String> labelList = new ArrayList<>();
    for (EntityAnnotation label : blobAnalysis) {
      if (label.getScore() >= MIN_SCORE_THRESHOLD) {
        labelList.add(label.getDescription());
      }
    }
    String labelSentence = String.join(", ", labelList);
    return addToDatastore(labelSentence);
  }

  /**
   * Adds the salient keywords from the analysis of a textual user input into the datastore.
   * This method overloads addKeywords() mainly to streamline how keywords are extracted
   * and added into the datastore. Instead of having to call addToDatastore(message) for one
   * class, and addKeywords(blobAnalysis) for another, it makes more sense and is easier to 
   * call just addKeywords(parameter) in both classes.
   * @return the key pointing to the newly added datastore entity.
   */
  public static String addKeywords(String message) throws IOException {
    return addToDatastore(message);
  }
  
  /**
   * Adds salient keywords from the message into the datastore.
   */
  private static String addToDatastore(String message) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    AnalyzeEntitiesResponse entityResponse = analyzeEntity(message);
    List<com.google.cloud.language.v1.Entity> entities = entityResponse.getEntitiesList();
    TreeSet<com.google.cloud.language.v1.Entity> orderSet = 
        new TreeSet<>((com.google.cloud.language.v1.Entity o1, com.google.cloud.language.v1.Entity o2) -> {
      if (o1.getSalience() < o2.getSalience()) {
        return 1;
      } else if (o1.getSalience() > o2.getSalience()) {
        return -1;
      } else {
        return 0;
      }
    });
    for (com.google.cloud.language.v1.Entity entity : entities) {
      if (entity.getSalience() < MIN_SALIENCE_THRESHOLD) {
        continue;
      }
      orderSet.add(entity);
    }
    // We must create a new list of Strings as a collection of NLP Entities is not supported by datastore.
    // We can limit the collection to 10 keywords, map every entity to their names, and only allow distinct values. 
    Collection<String> keywordCollection = 
        orderSet.stream().limit(MAX_NUM_KEYWORDS).map(e -> e.getName().toUpperCase()).distinct().collect(Collectors.toList());

    // Add the keyword collection to the datastore, marked by the User ID.
    UserService userService = UserServiceFactory.getUserService();
    final String userId = userService.getCurrentUser().getUserId();
    Entity datastoreEntity = new Entity("Keyword");
    datastoreEntity.setProperty("keywords", keywordCollection);
    datastoreEntity.setProperty("id", userId);
    datastore.put(datastoreEntity);
    return KeyFactory.keyToString(datastoreEntity.getKey());
  }

  /**
   * @return a response to the analyze-entity request.
   */ 
  private static AnalyzeEntitiesResponse analyzeEntity(String message) throws IOException {
    try (LanguageServiceClient language = LanguageServiceClient.create()) {
      Document doc = Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
      AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
          .setDocument(doc)
          .setEncodingType(EncodingType.UTF16)
          .build();
      AnalyzeEntitiesResponse response = language.analyzeEntities(request);
      return response;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
  /**
   * Given the user's ID, the method will return a map containing all the datastore keys of 
   * the user's previous keyword inputs, mapped to their respective list of keywords.
   */
  private static Map<String, Collection<String>> getKeyToKeywordMap(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Keyword").setFilter(
        new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Map<String, Collection<String>> keyKeywordMap = new HashMap<>();
    for (Entity entity : results.asIterable()) {
      Collection<String> keywords = (Collection<String>) entity.getProperty("keywords");

      // Map the key to its respective collection of keywords
      keyKeywordMap.put(KeyFactory.keyToString(entity.getKey()), keywords);
    }
    return keyKeywordMap;
  }
}
