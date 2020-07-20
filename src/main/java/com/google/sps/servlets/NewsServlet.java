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

/** Servlet that gets news information. */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> terms = Arrays.asList(request.getParameterValues("key"));
    HashMap<String, List<Article>> articleMap = new HashMap<>();
    terms.forEach((term) -> {
      try {
        String HTMLString = getHTML(term);
        List<Article> articleList = makeArticleList(HTMLString);
        articleMap.put(term, articleList);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    });
    String json = encodeBookMapAsJson(articleMap);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }

  public String getHTML(String term) throws IOException {
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("q", UrlRequest.encodeTerm(term));
    paramMap.put("hl", "en-US");
    paramMap.put("gl", "US");
    paramMap.put("ceid", "US:en");
    String basePath = "https://news.google.com/search?";
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
  }

  public List<Article> makeArticleList(String HTMLString) {
    List<Article> articleList = new ArrayList<>();
    final String articleTag = "DY5T1d";
    String[] substrings = HTMLString.split(articleTag);
    String substring;
    int closeBracket, openBracket;
    for (int i = 5; i < substrings.length; i++) {
      substring = substrings[i];
      closeBracket = substring.indexOf(">");
      openBracket = substring.indexOf("<");
      articleList.add(new Article(substring.substring(closeBracket + 1, openBracket)));
    }
    return articleList;
  }

  public String encodeBookMapAsJson(Map<String, List<Article>> map) {
    Gson gson = new Gson();
    Map<String, Object> newMap = new HashMap<>();
    newMap.put("articles", map);
    String json = gson.toJson(map); 
    return json;
  }
}