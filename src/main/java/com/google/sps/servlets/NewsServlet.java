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
@WebServlet("/news")
public class NewsServlet extends HttpServlet {

  private final int NUM_BOOKS_PER_KEYWORD = 3;
  private List<String> keywords = new ArrayList<String>(
    List.of("Black Lives Matter", "COVID-19")
  );
  private LinkedHashMap<String, List<Book>> booksMap = new LinkedHashMap<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String encodedKeyword;
    for (String keyword : keywords) {
      encodedKeyword = encodeTerm(keyword);
      addBooksForTerm(keyword, encodedKeyword);
    }
    String json = makeNewsJson();
    System.out.println(json); // test
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
    Map<String, Object> books = new HashMap<>();
    books.put("articles", booksMap);
    String json = gson.toJson(books); 
    return json;
  }

 /**
  * Adds Book object to books from JSON results of Guardian query.
  */
  private void jsonToBooks(String jsonString, String keyTerm) {
    System.out.println("In jsonToBooks for " + keyTerm); // test
    JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject();
    // Check to see if json has enough entries.
    int numResults = responseObject.get("totalItems").getAsInt();
    int resultsToShow = NUM_BOOKS_PER_KEYWORD;
    if (numResults < resultsToShow) {
      if (numResults > 0) {
        resultsToShow = numResults;
      } else {
        System.out.println("Error: no results found for query");
        return;
      }
    }
    JsonArray booksJsonArray = responseObject.getAsJsonArray("items");
    List<Book> books = new ArrayList<Book>();
    for (int i = 0; i < resultsToShow; i++) {
      JsonObject bookJson = booksJsonArray.get(i).getAsJsonObject().getAsJsonObject("volumeInfo"); 
      String title = bookJson.get("title").getAsString();
      System.out.println("title is " + title);
      String link = bookJson.get("infoLink").getAsString();
      System.out.println("link is " + link);
      //String date = bookJson.get("publishedDate").getAsString();
      //String image = bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
      //String description = bookJson.get("description").getAsString();
      //String writer = formatWriters(bookJson.getAsJsonArray("authors"));
      //books.add(new Book.Builder(title, link).withDate(date).withImage(image).withDescription(description).withWriter(writer).build());
      books.add(new Book.Builder(title,link).build());
      System.out.println(books); // test
    }
    booksMap.put(keyTerm, books);
    System.out.println(booksMap);
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
      returnString += writersArray.get(i).getAsString() + ", " ;
    }
    returnString += writersArray.get(length - 1).getAsString();
    return returnString;
  }

 /**
  * Adds a Book object in books for a key term.
  */
  private void addBooksForTerm(String keyTerm, String encodedKeyTerm) {
    String path = "https://www.googleapis.com/books/v1/volumes?";
    String queryParam = "q=" + encodedKeyTerm; 
    String queryPath = path + queryParam;
    System.out.println(queryPath);
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
        System.out.println("Error: connection response code is: " + responseCode);
      }    
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
