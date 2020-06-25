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

/**
 * Loads news
 */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {

  private List<String> keywords = new ArrayList<String>();
  private List<Article> articles = new ArrayList<Article>(
      List.of(
        new ArticleBuilder("Bolton says Trump turned a blind eye to the coronavirus pandemic")
          .withLink("https://www.cnn.com/2020/06/24/politics/john-bolton-interview-cnntv/index.html")
          .build(),
        new ArticleBuilder("Hearing goes off the rails when lawmaker keeps banging table")
          .withLink("https://www.cnn.com/videos/politics/2020/06/24/louie-gohmert-bangs-table-judiciary-hearing-vpx.cnn")
          .build()
      )   
  );

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = makeNewsJson();
    System.out.println(json); // test
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /*
    Article firstArticle = new ArticleBuilder("Bolton says Trump turned 'a blind eye' to the coronavirus pandemic")
    .withLink("https://www.cnn.com/2020/06/24/politics/john-bolton-interview-cnntv/index.html")
    .build();
    Article secondArticle = new ArticleBuilder("Hearing goes off the rails when lawmaker keeps banging table")
    .withLink("https://www.cnn.com/videos/politics/2020/06/24/louie-gohmert-bangs-table-judiciary-hearing-vpx.cnn")
    .build();
    articles.add(firstArticle);
    articles.add(secondArticle);
    response.sendRedirect("/news.html");
    */
  }

  private String makeNewsJson() {
    Gson gson = new Gson();
    String json = gson.toJson(articles);
    json = "{\"articles\": " + json + "}";  
    return json;
  }

  private class Article {

    private String title;
    private String link;

    private Article(String title, String link) {
      this.title = title;
      this.link = link;
    }
  }

  private class ArticleBuilder {

    private String title;
    private String link;

    private ArticleBuilder(String title) {
      this.title = title;
    }

    private ArticleBuilder withLink(String link) {
      this.link = link;
      return this;
    }
  
    private Article build() {
      return new Article(this.title, this.link);
    }
  }  

}
