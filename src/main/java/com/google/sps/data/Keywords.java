package com.google.sps.data;

import java.io.IOException;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Provide methods for the storage, extraction, and retrieval of keywords.
 */
public final class Keywords {

  private static final int MAX_NUM_KEYWORDS = 10;
  public static final String KEY = "keywords";

  /**
   * @return a list of the 10 most salient keywords
   */
  public static Set<String> getKeywords() {
    Query query = new Query(KEY).addSort("salience", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    HashSet<String> keywordSet = new HashSet<>();
    int index = 0;
    for (com.google.appengine.api.datastore.Entity entity : results.asIterable()) {
      keywordSet.add((String) entity.getProperty("keyword"));

      index++;
      if (index >= MAX_NUM_KEYWORDS) {
        break;
      }
    }
    return keywordSet;
  }

  /**
   * Adds the labels from the analysis of an image into the datastore.
   */
  public static void addKeywords(List<EntityAnnotation> blobAnalysis) throws IOException {
    String labels = "";
    for (EntityAnnotation label : blobAnalysis) {
      labels += label.getDescription() + ", ";
    }
    addToDatastore(labels);
  }

  /**
   * Adds the salient keywords from the analysis of a textual user input into the datastore.
   */
  public static void addKeywords(String message) throws IOException {
    addToDatastore(message);
  }
  
  /**
   * Adds salient keywords from the message into the datastore.
   */
  private static void addToDatastore(String message) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    AnalyzeEntitiesResponse entityResponse = analyzeEntity(message);
    List<Entity> entities = entityResponse.getEntitiesList();
    System.out.println(entities.size());
    for (Entity entity : entities) {
      if (entity.getSalience() == 0) {
        continue;
      }
 
      com.google.appengine.api.datastore.Entity datastoreEntity = 
          new com.google.appengine.api.datastore.Entity(KEY);
      datastoreEntity.setProperty("keyword", entity.getName());
      datastoreEntity.setProperty("salience", entity.getSalience());
      datastore.put(datastoreEntity);
    }
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
    }
  }
}

