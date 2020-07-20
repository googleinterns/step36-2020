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

/** */
@RunWith(JUnit4.class)
public final class NewsTest {
  
  @Test
  public void sanityTest() throws IOException {
    NewsServlet ns = new NewsServlet();
    List<Article> articleList = ns.makeArticleList(ns.getHTML("Black Lives Matter"));
    for (Article article : articleList) {
      System.out.println(article.getTitle());
    }
    Assert.assertEquals(1,1);
  }
  
}