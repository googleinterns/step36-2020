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
import com.google.sps.data.Book;
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
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.sps.data.UrlRequest;
import java.util.Collections;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Servlet for generating a map of each term to a 
 * list of the corresponding related books.
 */
@WebServlet("/books")
public class BooksServlet extends HttpServlet {

  private final int NUM_BOOKS_PER_TERM = 5;
  private static final String API_KEY = "AIzaSyD7NSsRElnqx6MTVSIq0-lRYe2sl2nosAk";
  private static final String API_PATH = "https://www.googleapis.com/books/v1/volumes";

  /*
  * Writes a mapping of terms to lists of book objects in the servlet response.
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] terms = request.getParameterValues("key");
    LinkedHashMap<String, List<Book>> booksMap = new LinkedHashMap<>();
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("key", API_KEY);
    queryParams.put("q", "");
    for (String term : terms) {
      queryParams.replace("q", term);
      String jsonString = UrlRequest.urlQuery(API_PATH, queryParams);
      if(jsonString.equals("")){
        booksMap.put(term, Collections.emptyList());
      } else {
        List<Book> books = makeBooksList(jsonString);
        booksMap.put(term, books);
      }
    }
    String json = encodeBookMapAsJson(booksMap);
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

 /**
  * Encodes a hashmap to a string format which can be easily processed by Mustache.
  * @return An encoded hashmap in string format.
  */
  private String encodeBookMapAsJson(LinkedHashMap<String, List<Book>> booksMap) {
    Gson gson = new Gson();
    Map<String, Object> books = new HashMap<>();
    books.put("books", booksMap);
    String json = gson.toJson(books); 
    return json;
  }

 /**
  * Creates list of Book objects from JSON results of Google Books query.
  * @return list of book objects.
  */
  private List<Book> makeBooksList(String jsonString) {
    JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject();
    JsonArray booksJsonArray = responseObject.getAsJsonArray("items");
    // Reduce number of books displayed if there less books in the json array than as default.
    int numResults = booksJsonArray.size();
    numResults = Math.min(numResults, NUM_BOOKS_PER_TERM);
    List<Book> books = new ArrayList<>();
    for (int i = 0; i < numResults; i++) {
      JsonObject bookJson = booksJsonArray.get(i).getAsJsonObject().getAsJsonObject("volumeInfo"); 
      String title = bookJson.get("title").getAsString();
      String link = bookJson.get("infoLink").getAsString();
      String image = bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
      String description = bookJson.get("description").getAsString();
      String author = formatAuthors(bookJson.getAsJsonArray("authors"));
      books.add(new Book.Builder(title, link).withImage(image).withDescription(description).withAuthor(author).build());
    }
    return books;
  }

 /**
  * Reads in JSON from an open URL.
  * @return JSON in String format.
  */
  private String readJsonFile(URL url) throws IOException {
    Scanner jsonReader = new Scanner(url.openStream());
    String data = "";
    while(jsonReader.hasNext()) {
        data += jsonReader.nextLine();
    }
    jsonReader.close();
    return data;
  }

 /**
  * Formats authors into string from JSON array.
  * @return authors in String fomat.
  */
  private String formatAuthors(JsonArray authorsArray) {
    Gson gson = new Gson();
    ArrayList jsonObjList = gson.fromJson(authorsArray, ArrayList.class);
    return String.join(",", jsonObjList);
  }

 /**
  * Makes request to Google Books API's for books relating to a term and gets JSON response.
  * @return String of Google Books API JSON response. 
  */
  private String getJsonStringForTerm(String term) {
    String path = "https://www.googleapis.com/books/v1/volumes?";
    String queryParam = "q=" + encodeTerm(term)+"&key="+API_KEY; 
    String queryPath = path + queryParam;
    try {
      URL url = new URL(queryPath);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; CrOS x86_64 13020.87.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.119 Safari/537.36");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestMethod("GET");
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode != 200) {
        System.err.println("Error: connection response code is: " + responseCode);
      }
      InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
      StringBuilder contentBuilder = new StringBuilder();
      try (BufferedReader inputReader = new BufferedReader(inputStream)) {
        String inputLine;
        while ((inputLine = inputReader.readLine()) != null) {
          contentBuilder.append(inputLine);
        }
      } catch(IOException e) {
        System.out.println(e);
        return "";
      } finally {
        connection.disconnect();
      }
      return contentBuilder.toString(); 
    } catch(Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
