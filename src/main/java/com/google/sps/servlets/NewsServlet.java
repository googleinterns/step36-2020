package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.Article;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Servlet for generating news articles from keywords.
 */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {

  private String API_KEY = "test";
  private int numArticlesPerKeyword = 3;
  private List<String> keywords = new ArrayList<String>(
    List.of("Black Lives Matter", "COVID-19")
  );
  private HashMap<String, List<Article>> articleMap = new HashMap<String, List<Article>>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    for (String keyword : keywords) {
      keyword = encodeTerm(keyword);
      addArticlesForTerm(keyword);
    }
    String json = makeNewsJson();
    response.getWriter().println(json);
  }

 /**
  * Encodes the given term so that it can fit in a url.
  * @return An encoded string
  */
  private String encodeTerm(String term) {
    try {
      return URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex.getCause());
    }
  }

  private String makeNewsJson() {
    Gson gson = new Gson();
    String json = gson.toJson(articleMap);
    json = "{\"articles\": " + json + "}";  
    return json;
  }

 /**
  * Adds Article object to articles from JSON results of Guardian query.
  */
  private void jsonToArticles(String jsonString, String keyTerm) {
    JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject().getAsJsonObject("response");
    // Check to see if json has valid status and enough entries.
    String status = responseObject.get("status").getAsString();
    int numResults = responseObject.get("total").getAsInt();
    int resultsToShow = numArticlesPerKeyword;
    if (!status.equals("ok")) {
      System.out.println("Error: status of JSON returned from Guardian API is " + status);
      return;
    }
    else if (numResults < numArticlesPerKeyword) {
      if (numResults > 0) {
        resultsToShow = numResults;
      }
      else {
        System.out.println("Error: no results found for query");
        return;
      }
    }
    else {
      JsonArray storiesJsonArray = responseObject.getAsJsonArray("results");
      List<Article> articles = new ArrayList<Article>();
      for (int i = 0; i < resultsToShow; i++) {
        JsonObject firstStoryJson = storiesJsonArray.get(i).getAsJsonObject();
        String title = firstStoryJson.get("webTitle").getAsString();
        String link = firstStoryJson.get("webUrl").getAsString();
        String date = firstStoryJson.get("webPublicationDate").getAsString();
        date = date.substring(0, date.indexOf("T"));
        articles.add(new Article.Builder(title, link).withDate(date).build());
      }
      articleMap.put(keyTerm, articles);
    }
  }

 /**
  * Reads in JSON from an open URL.
  * @return JSON in String format.
  */
  private String getJson(URL url) throws IOException {
    Scanner jsonReader = new Scanner(url.openStream());
    String data = "";
    while(jsonReader.hasNext()) {
        data += jsonReader.nextLine();
    }
    jsonReader.close();
    return data;
  }

 /**
  * Adds an Article object in articles for a key term.
  */
  private void addArticlesForTerm(String keyTerm) {
    String path = "https://content.guardianapis.com/search";
    String queryParam = "?q=" + keyTerm; 
    String apiKeyParam = "&api-key=" + API_KEY;
    String queryPath = path + queryParam + apiKeyParam;
    try {
      URL url = new URL(queryPath);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        String json = getJson(url);
        jsonToArticles(json, keyTerm);
      }
      else {
        System.out.println("Error: connection response code is: " + responseCode);
      }    
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
