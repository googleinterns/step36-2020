package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Given a tweet or sentence, the servlet will extract salient terms
 * and store them in a list.
 */
@WebServlet("/keyword")
public class KeywordServlet extends HttpServlet {

  private static final List<String> tweets = new ArrayList<>();
  private static final List<String> terms = Arrays.asList("Black Lives Matter", "COVID-19");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Extract saliency.
    String json = jsonBuilder(terms);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String tweet = request.getParameter("keyword-sentence");
    if (tweet != null && !tweet.equals("")) {
      tweets.add(request.getParameter("keyword-sentence"));
    }
    response.sendRedirect("/main.html");
  }

  /**
   * Generates and returns a JSON usable by Mustache.
   */
  private String jsonBuilder(Collection<String> toJson) {
    List<String> terms = new ArrayList<>();
    for (String term : toJson) {
      terms.add(keyValueJson("term", term));
    }

    return String.format("{\"keywords\": [%s]}", String.join(",", terms));
  }

  /**
   * Given a key-value pair, this function returns "{key: value}".
   */
  private String keyValueJson(String key, String value) {
    return String.format("{\"%s\": \"%s\"}", key, value);
  }
}
