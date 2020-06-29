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

/**
 * Servlet for generating news articles from keywords.
 */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {
  private List<String> keywords = new ArrayList<>();
  
  // Hardcoded list of articles. 
  // In future implementation, will be generated based on key words.
  private List<Article> articles = new ArrayList<>(
      List.of(
        new Article.Builder("Bolton says Trump turned a blind eye to the coronavirus pandemic",
        "https://www.cnn.com/2020/06/24/politics/john-bolton-interview-cnntv/index.html")
          .withWriter("Zachary Cohen")
          .withDate("06/24/2020")
          .build(),
        new Article.Builder("Hearing goes off the rails when lawmaker keeps banging table",
        "https://www.cnn.com/videos/politics/2020/06/24/louie-gohmert-bangs-table-judiciary-hearing-vpx.cnn")
          .withDate("06/24/2020")
          .build()
      )   
  );

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = makeNewsJson();
    response.getWriter().println(json);
  }

  private String makeNewsJson() {
    Gson gson = new Gson();
    String json = gson.toJson(articles);
    json = String.format("{\"articles\": %s}", json);
    return json;
  }
}
