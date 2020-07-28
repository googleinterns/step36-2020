package com.google.sps.servlets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import java.util.List;
import java.util.ArrayList;
import com.google.sps.data.Article;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;


@RunWith(JUnit4.class)
public final class NewsTest {
  
  public final static String HTML_PATH_PREFIX = "src/test/java/com/google/sps/data/";
  public final static String FULL_HTML_PATH = HTML_PATH_PREFIX + "fullNewsHTML.txt";
  public final static String PART_HTML_PATH = HTML_PATH_PREFIX + "partialNewsHTML.txt";
  public final static String EMPTY_HTML_PATH = HTML_PATH_PREFIX + "emptyHTML.txt";
  public NewsServlet ns;
  public static List<Article> someArticleList = new ArrayList<>(); 
  public static List<Article> fullArticleList = new ArrayList<>();

 /**
  * Helper method that converts a file path of a text file to a String.
  * @return String version of text in specified file.
  */
  private static String textToString(String filePath) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader inputReader = new BufferedReader(new FileReader(filePath))) {
      String inputLine;
      while ((inputLine = inputReader.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    } catch(IOException e) {
      System.err.println(e);
      return "";
    }
    return stringBuilder.toString();
  }

 /**
  * Helper method to check the makeArticleList method in the NewsServlet class.
  * Checks if makeArticleList on HTML text in the specified file has output
  * equivalent to expectedList.
  */
  private void checkMakeArticleList(String filePath, List<Article> expectedList) throws IOException {
    String htmlString = textToString(filePath);
    List<Article> articleList = ns.makeArticleList(htmlString);
    // Check each class instance in each article object is equal.
    Assert.assertEquals(articleList.size(), expectedList.size());
    for(int i = 0; i < articleList.size(); i++) {
      Article realArticle = articleList.get(i);
      Article expectedArticle = expectedList.get(i);  
      Assert.assertEquals(expectedArticle.getTitle(), realArticle.getTitle());
      Assert.assertEquals(expectedArticle.getLink(), realArticle.getLink());
    }  
  }
  
 /**
  * Set up for following tests.
  * Populates someArticleList with Article entries.
  */
  @BeforeClass
  public static void setUp() {
    someArticleList.add(new Article(
      "The latest Instagram trend: putting your name on photos of frogs, cats and Harry Styles",
      "http://news.google.com/articles/CBMib2h0dHBzOi8vd3d3LnRoZWd1YXJkaWFuLmNvbS91cy1uZXdzLzIwMjAvanVsLzIwL2luc3RhZ3JhbS13aGF0LWZyb2ctYXJlLXlvdS1pbnNpZGUtdGhlLWxhdGVzdC13YXktdG8td2FzdGUtdGltZdIBb2h0dHBzOi8vYW1wLnRoZWd1YXJkaWFuLmNvbS91cy1uZXdzLzIwMjAvanVsLzIwL2luc3RhZ3JhbS13aGF0LWZyb2ctYXJlLXlvdS1pbnNpZGUtdGhlLWxhdGVzdC13YXktdG8td2FzdGUtdGltZQ?hl=en-US&amp;gl=US&amp;ceid=US%3Aen"
    ));
    someArticleList.add(new Article(
      "How a blue protein turns tree frogs bright green",
      "http://news.google.com/articles/CBMiVmh0dHBzOi8vd3d3LnNjaWVuY2VtYWcub3JnL25ld3MvMjAyMC8wNy9ob3ctYmx1ZS1wcm90ZWluLXR1cm5zLXRyZWUtZnJvZ3MtYnJpZ2h0LWdyZWVu0gEA?hl=en-US&amp;gl=US&amp;ceid=US%3Aen"
    ));
  }

  @Before
  public void createNewsServlet() throws IOException {
    ns = new NewsServlet();
  }

 /**
  * Check that when given no HTML text, makeArticleList returns an empty list.
  */
  @Test
  public void articlesFromEmptyHTML() throws IOException {
    List<Article> articleList = ns.makeArticleList("");
    Assert.assertEquals(articleList, new ArrayList<Article>());
  }

 /**
  * Checks that makeArticleList makes a list with all the articles in the HTML
  * text if there are less articles in the HTML text than makeArticleList would
  * genereate by default.
  */
  @Test
  public void articlesFromPartialHTML() throws IOException {
    checkMakeArticleList(PART_HTML_PATH, someArticleList);
  }

 /**
  * Checks that makeArticleList makes correctly sized list when there are more
  * articles in the HTML text than makeArticleList would generate by default.
  */
  @Test
  public void articlesFromFullHTML() throws IOException {  
    List<Article> fullArticleList = new ArrayList<>();
    fullArticleList.add(someArticleList.get(0));
    fullArticleList.add(someArticleList.get(1));
    fullArticleList.add(new Article(
      "How frogs became green — again, and again, and again : Research Highlights", 
      "http://news.google.com/articles/CBMiMmh0dHBzOi8vd3d3Lm5hdHVyZS5jb20vYXJ0aWNsZXMvZDQxNTg2LTAyMC0wMjEwMy160gEA?hl=en-US&amp;gl=US&amp;ceid=US%3Aen"
    ));
    checkMakeArticleList(FULL_HTML_PATH, fullArticleList);
  }

 /**
  * Checks that getHTML method returns empty string when no HTML text.
  */
  @Test
  public void getEmptyHTML() {
    try {
      HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
      Mockito.when(connection.getInputStream()).thenReturn(new FileInputStream(EMPTY_HTML_PATH));
      Assert.assertEquals(ns.getHTML(connection), "");  
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

 /**
  * Checks that getHTML method translates HTML text to String correctly under
  * normal conditions.
  */
  @Test
  public void getFullHTML() {
    try {
      HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
      Mockito.when(connection.getInputStream()).thenReturn(new FileInputStream(FULL_HTML_PATH));
      Assert.assertEquals(ns.getHTML(connection), textToString(FULL_HTML_PATH));  
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
