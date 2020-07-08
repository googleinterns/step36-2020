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

/**
 * Servlet for generating books from keywords.
 */
@WebServlet("/books")
public class BooksServlet extends HttpServlet {

  private final int NUM_BOOKS_PER_KEYWORD = 5;
  private List<String> keywords;
  private LinkedHashMap<String, List<Book>> booksMap = new LinkedHashMap<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    keywords = Arrays.asList(request.getParameterValues("key"));
    keywords.forEach((keyword) -> addBooksForTerm(keyword));
    String json = makeBooksJson();
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

  private String makeBooksJson() {
    Gson gson = new Gson();
    Map<String, Object> books = new HashMap<>();
    books.put("books", booksMap);
    String json = gson.toJson(books); 
    return json;
  }

 /**
  * Adds Book object to books from JSON results of Guardian query.
  */
  private void jsonToBooks(String jsonString, String keyTerm) {
    JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject();
    // Check to see if json has enough entries.
    int numResults = responseObject.get("totalItems").getAsInt();
    int resultsToShow = NUM_BOOKS_PER_KEYWORD;
    if (numResults < resultsToShow) {
      if (numResults > 0) {
        resultsToShow = numResults;
      } else {
        System.err.println("Error: no results found for query");
        return;
      }
    }
    JsonArray booksJsonArray = responseObject.getAsJsonArray("items");
    List<Book> books = new ArrayList<>();
    for (int i = 0; i < resultsToShow; i++) {
      JsonObject bookJson = booksJsonArray.get(i).getAsJsonObject().getAsJsonObject("volumeInfo"); 
      String title = bookJson.get("title").getAsString();
      String link = bookJson.get("infoLink").getAsString();
      String image = bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
      String description = bookJson.get("description").getAsString();
      String writer = formatWriters(bookJson.getAsJsonArray("authors"));
      books.add(new Book.Builder(title, link).withImage(image).withDescription(description).withWriter(writer).build());
    }
    booksMap.put(keyTerm, books);
    System.out.println(booksMap); // test
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
  * Formats writers into string from JSON array.
  * @return writers in String fomat.
  */
  private String formatWriters(JsonArray writersArray) {
    String returnString = "";
    int length = writersArray.size();
    for (int i = 0; i < length - 1; i++){
      returnString += writersArray.get(i).getAsString() + ", ";
    }
    returnString += writersArray.get(length - 1).getAsString();
    return returnString;
  }

 /**
  * Adds a Book object in books for a key term.
  */
  private void addBooksForTerm(String keyTerm) {
    String path = "https://www.googleapis.com/books/v1/volumes?";
    String queryParam = "q=" + encodeTerm(keyTerm); 
    String queryPath = path + queryParam;
    try {
      URL url = new URL(queryPath);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        String json = getJson(url);
        jsonToBooks(json, keyTerm);
      } else {
        System.err.println("Error: connection response code is: " + responseCode);
      }    
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
