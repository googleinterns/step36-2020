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

/** Servlet that gets news information. */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {

  public final String GOOGLE_NEWS_PATH = "https://news.google.com/search";
  public final String GOOGLE_SEARCH_PATH = "https://www.google.com/search";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> terms = Arrays.asList(request.getParameterValues("key"));
    HashMap<String, List<Article>> articleMap = new HashMap<>();
    terms.forEach((term) -> {
      try {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("q", UrlRequest.encodeTerm(term));
        paramMap.put("hl", "en-US");
        paramMap.put("gl", "US");
        paramMap.put("ceid", "US:en");
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
    final String articleTag = "DY5T1d";
    String[] substrings = HTMLString.split(articleTag);
    String thisSubstring, lastSubstring, title, link;
    int closeBracket, openBracket;
    for (int i = 5; i < substrings.length; i++) {
      lastSubstring = substrings[i - 1];
      thisSubstring = substrings[i];
      title = thisSubstring.substring(thisSubstring.indexOf(">") + 1, thisSubstring.indexOf("<"));
      link = lastSubstring.substring(lastSubstring.lastIndexOf("href"), lastSubstring.lastIndexOf("\""));
      link = "news.google.com" + link.substring(link.indexOf(".") + 1, link.lastIndexOf("\""));
      articleList.add(new Article(title, link));
    }
    return articleList;
  }

  public String encodeMapAsJson(Map<String, List<Article>> map) {
    Gson gson = new Gson();
    Map<String, Object> newMap = new HashMap<>();
    newMap.put("articles", map);
    String json = gson.toJson(map); 
    return json;
  }
}
