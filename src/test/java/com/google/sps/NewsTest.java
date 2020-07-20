package com.google.sps.servlets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.google.sps.data.Article;
import com.google.sps.data.UrlRequest;

/** */
@RunWith(JUnit4.class)
public final class NewsTest {
  
  @Test
  public void sanityTest() throws IOException {
    NewsServlet ns = new NewsServlet();
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("q", UrlRequest.encodeTerm("Black Lives Matter"));
    paramMap.put("hl", "en-US");
    paramMap.put("gl", "US");
    paramMap.put("ceid", "US:en");
    List<Article> articleList = ns.makeArticleList(ns.getHTML(ns.getConnection(ns.GOOGLE_NEWS_PATH, paramMap)));
    for (Article article : articleList) {
      System.out.println(article.getTitle());
      System.out.println(article.getLink());
    }
    Assert.assertEquals(1,1);
  }
  
}