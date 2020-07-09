package com.google.sps.servlets;

import java.io.IOException;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Given a tweet or sentence, the servlet will extract salient terms
 * and store them in a list.
 */
@WebServlet("/keyword")
public class KeywordServlet extends HttpServlet {

  private final List<String> tweets = new ArrayList<>();
  private final TreeMap<Entity, Float> terms = new TreeMap<>((term1, term2) -> {
    // Sort the Entity keys by their salience scores
    if (term1.getSalience() > term2.getSalience()) {
      return 1;
    } else if (term1.getSalience() < term2.getSalience()) {
      return -1;
    } else {
      return 0;
    }
  });

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    for (String tweet : tweets) {
      AnalyzeEntitiesResponse entityResponse = analyzeEntity(tweet);
      List<Entity> entities = entityResponse.getEntitiesList();
      for (Entity entity : entities) {
        if (entity.getSalience() == 0) {
          continue;
        }
       
        // We are only looking at (at most) the 10 most salient keywords. 
        if (terms.size() < 10) {
          if (terms.containsKey(entity)) {  // Keep the max salience score if the term already exists in the map.
            terms.put(entity, Math.max(entity.getSalience(), terms.get(entity)));
            continue;
          }
          terms.put(entity, entity.getSalience());
        } else if (!terms.containsKey(entity) && entity.getSalience() > terms.get(terms.pollFirstEntry())) {
          terms.remove(terms.pollFirstEntry());
          terms.put(entity, entity.getSalience());
        } else {}
      }
    }
    
    // TODO: Retrieve tweets from datastore.
    List<String> keywords = new ArrayList<>();
    for (Entity entity : terms.keySet()) {
      keywords.add(entity.getName());
    }
    Gson gson = new Gson();
    String json = gson.toJson(keywords);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String tweet = request.getParameter("keyword-sentence");
    if (tweet != null && !tweet.equals("")) {
      // TODO: Add tweets to a datastore.
      tweets.add(request.getParameter("keyword-sentence"));
    }
    response.sendRedirect("/main.html");
  }

  /**
   * @return a response to the analyze-entity request
   */ 
  private AnalyzeEntitiesResponse analyzeEntity(String message) throws IOException {
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
