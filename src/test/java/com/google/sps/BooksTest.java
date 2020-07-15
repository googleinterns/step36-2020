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

/** */
@RunWith(JUnit4.class)
public final class BooksTest {
  
  public String EMPTY_MAP_STRING = "{}";
  
 /**
  * Checks that if the keyword is gibberish, no result is returned.
  */
  @Test
  public void noOutputTest() throws IOException {
    Map<String, String[]> badParams = new HashMap<>();
    String[] gibberish = {"kjdaflkdsalklkjfdsalkflkds"};
    badParams.put("key", gibberish);
    ServletSetUpObject booksGet = TestingUtil.mockGet(badParams);
    BooksServlet booksServlet = new BooksServlet();
    booksServlet.doGet(booksGet.getRequest(), booksGet.getResponse());
    String output = booksGet.getWriter().toString();
    output = TestingUtil.getInnerMap(output);
    Assert.assertEquals(output, EMPTY_MAP_STRING);
  }

 /**
  * Checks that reasonable keywords produce non-empty result.
  */ 
  @Test
  public void someOutputTest() throws IOException {
    Map<String, String[]> goodParams = new HashMap<>();
    String[] keyWords = {"water", "air"};
    goodParams.put("key", keyWords);
    ServletSetUpObject booksGet = TestingUtil.mockGet(goodParams);
    BooksServlet booksServlet = new BooksServlet();
    booksServlet.doGet(booksGet.getRequest(), booksGet.getResponse());
    String output = booksGet.getWriter().toString();
    output = TestingUtil.getInnerMap(output);
    Assert.assertNotEquals(output, EMPTY_MAP_STRING);
  }
  
}
