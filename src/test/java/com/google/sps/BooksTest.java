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
  * Checks that if the keyword was nonsense, no result is returned.
  */
  @Test
  public void noOutputTest() throws IOException {
    Map<String, String[]> badParams = new HashMap<>();
    String[] gibberish = {"kjdaflkdsalklkjfdsalkflkds"};
    badParams.put("key", gibberish);
    SetUpObject booksGet = TestingUtil.mockGet(badParams);
    BooksServlet booksServlet = new BooksServlet();
    booksServlet.doGet(booksGet.getRequest(), booksGet.getResponse());
    String output = booksGet.getWriter().toString();
    output = TestingUtil.getInnerMap(output);
    System.out.println("noOutputTest() actual output is " + output);
    System.out.println("noOutputTest() expected output is " + EMPTY_MAP_STRING);
    Assert.assertTrue(output.equals(EMPTY_MAP_STRING));
  }

 /**
  * Checks that reasonable keywords produce non-empty result.
  */ 
  @Test
  public void someOutputTest() throws IOException {
    Map<String, String[]> goodParams = new HashMap<>();
    String[] keyWords = {"water", "air"};
    goodParams.put("key", keyWords);
    SetUpObject booksGet = TestingUtil.mockGet(goodParams);
    BooksServlet booksServlet = new BooksServlet();
    booksServlet.doGet(booksGet.getRequest(), booksGet.getResponse());
    String output = booksGet.getWriter().toString();
    output = TestingUtil.getInnerMap(output);
    System.out.println("someOutputTest() output is " + output);
    Assert.assertNotEquals(output, EMPTY_MAP_STRING);
  }
  
}
