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
 * Given a tweet or sentence, the servlet will extract salient terms from those
 * and store them in a list.
 */
@WebServlet("/keyword")
public class KeywordServlet extends HttpServlet {

  private static final List<String> tweets = new ArrayList<>();
  private static final List<String> terms = Arrays.asList("Black Lives Matter", "COVID-19");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Extract saliency
    Gson gson = new Gson();
    String json = gson.toJson(terms);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String tweet = request.getParameter("keyword-sentence");
    if (tweet != null && !tweet.equals("")) {
      tweets.add(request.getParameter("keyword-sentence"));
    }
    response.sendRedirect("/index.html");
  }

}

