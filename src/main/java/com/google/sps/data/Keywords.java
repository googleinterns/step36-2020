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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Provide methods for the storage, extraction, and retrieval of keywords.
 */
public final class Keywords {

  private static final int MAX_NUM_KEYWORDS = 10;

  /**
   * @return a list of the 10 most salient keywords
   */
  public static List<String> getKeywords(String key) {
    try {
      Key datastoreKey = KeyFactory.stringToKey(key);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity result = datastore.get(datastoreKey);
    
      Collection<String> keywordCollection = (Collection<String>) result.getProperty("keywords");
      List<String> keywordList = new ArrayList<>();
      int index = 0;
      for (String keyword : keywordCollection) {
        keywordList.add(keyword);
    
        index++;
        if (index >= MAX_NUM_KEYWORDS) {
          break;
        }
      }
      return keywordList;
    } catch (EntityNotFoundException ex) {
      return new ArrayList<>();
    }
  }

  /**
   * Adds the labels from the analysis of an image into the datastore.
   * @return the key pointing to the newly added datastore entity.
   */
  public static String addKeywords(List<EntityAnnotation> blobAnalysis) throws IOException {
    String labels = "";
    for (EntityAnnotation label : blobAnalysis) {
      labels += label.getDescription() + ", ";
    }
    return addToDatastore(labels);
  }

  /**
   * Adds the salient keywords from the analysis of a textual user input into the datastore.
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
      if (entity.getSalience() == 0) {
        continue;
      }
      orderSet.add(entity);
    }
    // We must create a new list of Strings as a collection of NLP Entities is not supported by datastore.
    List<String> keywordList = new ArrayList<>();
    for (com.google.cloud.language.v1.Entity entity : orderSet) {
      keywordList.add(entity.getName());
    }
    Entity datastoreEntity = new Entity("Keyword");
    datastoreEntity.setProperty("keywords", keywordList);
    datastore.put(datastoreEntity);
    return KeyFactory.keyToString(datastoreEntity.getKey());
  }

  /**
   * @return a response to the analyze-entity request
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
}

