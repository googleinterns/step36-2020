package com.google.sps.servlets;

import com.google.sps.data.Article;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import com.google.sps.data.UrlRequest;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.FileNotFoundException;

/** Servlet that gets news information. */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {

  public final String GOOGLE_NEWS_PATH = "https://news.google.com/search";
  public final String ARTICLE_TAG = "DY5T1d"; // Class tag for articles in Google News html code.
  public final int MAX_ARTICLES = 3;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> terms = Arrays.asList(request.getParameterValues("key"));
    HashMap<String, List<Article>> articleMap = new HashMap<>();
    terms.forEach((term) -> {
      try {
        System.out.println("In try loop");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("q", UrlRequest.encodeTerm(term));
        System.out.println("after encode term");
        //paramMap.put("gl", getCountry(request));
        HttpURLConnection connect = getConnection(GOOGLE_NEWS_PATH, paramMap);
        String HTMLString = getHTML(connect);
        List<Article> articleList = makeArticleList(HTMLString);
        articleMap.put(term, articleList);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    });
    String json = encodeMapAsJson(articleMap);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }

  public String getCountry(HttpServletRequest request) throws IOException {
    String latitude = request.getParameter("lat");
    String longitude = request.getParameter("lng");
    Map<String, String> locationQueryParams = new HashMap<>();
    locationQueryParams.put("lat", latitude);
    locationQueryParams.put("lng", longitude);
    String locationUrl = String.format("%s://%s:%s/location",
        request.getScheme(), 
        request.getServerName(), 
        request.getServerPort());
    String country = "US";  // Have US as default country.
    try {
      String locationJsonResult = UrlRequest.urlQuery(locationUrl, locationQueryParams);
      Gson gson = new Gson();
      Type mapType = new TypeToken<Map<String, String>>() {}.getType();
      Map<String, String> locationMap = gson.fromJson(locationJsonResult, mapType);
      country = locationMap.get("Short Country");
    } catch (FileNotFoundException fnfe) {
      // Location URL doesn't work on devserver, so instead use hardcoded string.
      fnfe.printStackTrace();
    }
    return country;
  }

  public HttpURLConnection getConnection(String basePath, Map<String, String> paramMap) throws IOException {
    String path = String.format("%s?%s", basePath, UrlRequest.getParamsString(paramMap));
    URL url = new URL(path);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "text/html");
    connection.setRequestProperty("Content-Type", "text/html");
    int responseCode = connection.getResponseCode();
    if (responseCode != 200) {
      System.err.println("Error: connection response code is: " + responseCode);
    }
    return connection;
  }

  public String getHTML(HttpURLConnection connection) throws IOException {
    InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
    StringBuilder contentBuilder = new StringBuilder();
    try (BufferedReader inputReader = new BufferedReader(inputStream)) {
      String inputLine;
      while ((inputLine = inputReader.readLine()) != null) {
        contentBuilder.append(inputLine);
      }
    } catch(IOException e) {
      System.err.println(e);
      return "";
    } finally {
      connection.disconnect();
    }
    return contentBuilder.toString();
  }

  public List<Article> makeArticleList(String HTMLString) throws IOException {
    List<Article> articleList = new ArrayList<>();
    String[] substrings = HTMLString.split(ARTICLE_TAG);
    String thisSubstring, lastSubstring, title, linkElement, link;
    // For each substring seperated by the article class tag.
    // Begins at index 4 because the article tag appears 4 times before the article elements appear in the HTML.
    for (int i = 4; i < Math.min(substrings.length, 4 + MAX_ARTICLES); i++) {
      // Get the link from before the article tag and the title from after to make a new Article object.
      // Ex. lastSubstring: ...<ahref="relative_link" class="
      // Ex. thisSubstring: ">Title<..
      lastSubstring = substrings[i - 1];
      thisSubstring = substrings[i];
      title = thisSubstring.substring(thisSubstring.indexOf(">") + 1, thisSubstring.indexOf("<"));
      linkElement = lastSubstring.substring(lastSubstring.lastIndexOf("href"), lastSubstring.lastIndexOf("\""));
      link = "http://news.google.com" + linkElement.substring(linkElement.indexOf(".") + 1, linkElement.lastIndexOf("\""));
      articleList.add(new Article(title, link));
    }
    return articleList;
  }

  public String encodeMapAsJson(Map<String, List<Article>> map) {
    Gson gson = new Gson();
    Map<String, Object> newMap = new HashMap<>();
    newMap.put("news", map);
    String json = gson.toJson(newMap); 
    return json;
  }
}
