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
    return UrlRequest.urlQuery("https://news.google.com/search?", paramMap);
  }

  public List<Article> makeArticleList(String HTMLString) {
    return new ArrayList<Article>();
  }

  private String encodeBookMapAsJson(Map<String, List<Article>> map) {
    Gson gson = new Gson();
    Map<String, Object> newMap = new HashMap<>();
    newMap.put("articles", map);
    String json = gson.toJson(map); 
    return json;
  }
}