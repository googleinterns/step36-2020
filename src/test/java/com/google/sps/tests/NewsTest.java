package com.google.sps.servlets;

import org.junit.Assert;
import org.junit.Before;
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


@RunWith(JUnit4.class)
public final class NewsTest {

  public final String FULL_HTML_PATH = "src/test/java/com/google/sps/data/fullNewsHTML.txt";
  public final String EMPTY_HTML_PATH = "src/test/java/com/google/sps/data/emptyHTML.txt";
  public List<Article> fullArticleList = new ArrayList<>(); 

  public static String textToString(String filePath) throws IOException {
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

  @Before
  public void setUp() {
    // Populate fullArticleList.
    fullArticleList.add(new Article(
      "The latest Instagram trend: putting your name on photos of frogs, cats and Harry Styles",
      "news.google.com/articles/CBMib2h0dHBzOi8vd3d3LnRoZWd1YXJkaWFuLmNvbS91cy1uZXdzLzIwMjAvanVsLzIwL2luc3RhZ3JhbS13aGF0LWZyb2ctYXJlLXlvdS1pbnNpZGUtdGhlLWxhdGVzdC13YXktdG8td2FzdGUtdGltZdIBb2h0dHBzOi8vYW1wLnRoZWd1YXJkaWFuLmNvbS91cy1uZXdzLzIwMjAvanVsLzIwL2luc3RhZ3JhbS13aGF0LWZyb2ctYXJlLXlvdS1pbnNpZGUtdGhlLWxhdGVzdC13YXktdG8td2FzdGUtdGltZQ?hl=en-US&amp;gl=US&amp;ceid=US%3Aen"
    ));
    fullArticleList.add(new Article(
      "How a blue protein turns tree frogs bright green",
      "news.google.com/articles/CBMiVmh0dHBzOi8vd3d3LnNjaWVuY2VtYWcub3JnL25ld3MvMjAyMC8wNy9ob3ctYmx1ZS1wcm90ZWluLXR1cm5zLXRyZWUtZnJvZ3MtYnJpZ2h0LWdyZWVu0gEA?hl=en-US&amp;gl=US&amp;ceid=US%3Aen"
    ));
    fullArticleList.add(new Article(
      "How frogs became green — again, and again, and again : Research Highlights", 
      "news.google.com/articles/CBMiMmh0dHBzOi8vd3d3Lm5hdHVyZS5jb20vYXJ0aWNsZXMvZDQxNTg2LTAyMC0wMjEwMy160gEA?hl=en-US&amp;gl=US&amp;ceid=US%3Aen"
    ));
  }

  @Test
  public void articlesFromEmptyHTML() throws IOException {
    NewsServlet ns = new NewsServlet();
    String htmlString = "";
    List<Article> articleList = ns.makeArticleList(htmlString);
    Assert.assertEquals(articleList, new ArrayList<Article>());
  }

  @Test
  public void articlesFromFullHTML() throws IOException {
    NewsServlet ns = new NewsServlet();
    String htmlString = textToString(FULL_HTML_PATH);
    List<Article> articleList = ns.makeArticleList(htmlString);
    // Check each class instance in each article object is equal.
    Assert.assertEquals(articleList.size(), fullArticleList.size());
    for(int i = 0; i < articleList.size(); i++) {
      Article realArticle = articleList.get(i);
      Article expectedArticle = fullArticleList.get(i);
      Assert.assertEquals(expectedArticle.getTitle(), realArticle.getTitle());
      Assert.assertEquals(expectedArticle.getLink(), realArticle.getLink());
    }   
  }

  @Test
  public void getEmptyHTML() {
    try {
      HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
      Mockito.when(connection.getInputStream()).thenReturn(new FileInputStream(EMPTY_HTML_PATH));
      NewsServlet ns = new NewsServlet();
      Assert.assertEquals(ns.getHTML(connection), textToString(EMPTY_HTML_PATH));  
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getFullHTML() {
    try {
      HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
      Mockito.when(connection.getInputStream()).thenReturn(new FileInputStream(FULL_HTML_PATH));
      NewsServlet ns = new NewsServlet();
      Assert.assertEquals(ns.getHTML(connection), textToString(FULL_HTML_PATH));  
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  

}
