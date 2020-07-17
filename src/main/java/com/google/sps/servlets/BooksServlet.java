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
import com.google.sps.data.GetAPIKeyUtil;
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
import java.util.Collections;

/**
 * Servlet for generating a map of each term to a 
 * list of the corresponding related books.
 */
@WebServlet("/books")
public class BooksServlet extends HttpServlet {

  private final int NUM_BOOKS_PER_TERM = 5;
  private static String API_KEY;

  /*
  * Writes a mapping of terms to lists of book objects in the servlet response.
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    GetAPIKeyUtil util = new GetAPIKeyUtil();
    API_KEY = util.getAPIKey("google");
    List<String> terms = Arrays.asList(request.getParameterValues("key"));
    LinkedHashMap<String, List<Book>> booksMap = new LinkedHashMap<>();
    terms.forEach((term) -> {
      try {
        String jsonString = getJsonStringForTerm(term);
        List<Book> books = makeBooksList(jsonString);
        booksMap.put(term, books);
      } catch(Exception e) {
        e.printStackTrace();
        booksMap.put(term, Collections.emptyList());
      }
    });
    String json = encodeBookMapAsJson(booksMap);
    response.setCharacterEncoding("UTF-8");
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
      Book.Builder bookBuilder = new Book.Builder(title, link);
      try {
        String image = bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
        bookBuilder.withImage(image);
      }
      catch (NullPointerException e) {
        e.printStackTrace();
      }
      try {
        String description = bookJson.get("description").getAsString();
        bookBuilder.withDescription(description);
      }
      catch (NullPointerException e) {
        e.printStackTrace();
      }
      try {
        String author = formatAuthors(bookJson.getAsJsonArray("authors"));
        bookBuilder.withAuthor(author);
      }
      catch (NullPointerException e) {
        e.printStackTrace();
      }
      books.add(bookBuilder.build());
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
    String queryParam = "q=" + encodeTerm(term)+"&key="+API_KEY+"&country=US"; 
    String queryPath = path + queryParam;
    try {
      URL url = new URL(queryPath);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode != 200) {
        System.err.println("Error: connection response code for Books API is: " + responseCode);
      }
      return readJsonFile(url);   
    } catch(Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
