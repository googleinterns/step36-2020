package com.google.sps.servlets;

import java.io.IOException;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.gson.Gson;
import java.util.Collection;
import com.google.sps.data.Keywords;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Given a tweet or sentence, the servlet will extract salient terms
 * and store them in a list.
 */
@WebServlet("/keyword")
public class KeywordServlet extends HttpServlet {

  private UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String key = request.getParameter("k");
    Collection<String> keywords = null;
    String language = null;
    if (userService.isUserLoggedIn()) {
      keywords = Keywords.getKeywords(key);
      language = Keywords.getLanguage(key);
    }
    Map<String, Collection<String>> combinedMap = new HashMap<>();
    combinedMap.put("keywords", keywords);
    Collection<String> languageList = new ArrayList();
    languageList.add(language);
    combinedMap.put("language", languageList);
    Gson gson = new Gson();
    String json = gson.toJson(combinedMap);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
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
