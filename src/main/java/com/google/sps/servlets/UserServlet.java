package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class UserServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    List<String> logInfo = new ArrayList<>();
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/");
      logInfo.add(logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL("/");
      logInfo.add(loginUrl);
      logInfo.add("placeholder");
    }
    Gson gson = new Gson();
    String json = gson.toJson(logInfo);
    response.getWriter().println(json);
  }
}
