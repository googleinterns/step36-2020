package com.google.sps.servlets;

import java.io.IOException;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gson.Gson;
import com.google.sps.data.Keywords;
import java.util.Set;
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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      String key = request.getParameter("k");
      List<String> keywords = Keywords.getKeywords(key);
      Gson gson = new Gson();
      String json = gson.toJson(keywords);
      response.getWriter().println(json);
    } catch (EntityNotFoundException enfe) {
      enfe.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String tweet = request.getParameter("keyword-sentence");
    String key = "";
    if (tweet != null && !tweet.equals("")) {
      key = Keywords.addKeywords(tweet);
    }
    response.sendRedirect(String.format("/results?k=%s", key));
  }
}

