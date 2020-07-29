package com.google.sps.servlets;

import java.io.IOException;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.gson.Gson;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.google.sps.data.Keywords;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/all-keywords")
public class AllKeywordsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    Map<String, Collection<String>> allKeywords = new HashMap<>();
    if (userService.isUserLoggedIn()) {
      allKeywords = Keywords.getAllKeywords();
    }
    Gson gson = new Gson();
    String json = gson.toJson(allKeywords);
    response.getWriter().println(json);
  }
}
