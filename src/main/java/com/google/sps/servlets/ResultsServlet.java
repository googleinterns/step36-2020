package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Serves the html template used in the results page with a datastore key. */
@WebServlet("/results")
public class ResultsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String key = request.getParameter("k");
    Map<String, String> map = new HashMap<>();
    DefaultMustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache;
	  if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/");
      map.put("key", key);
      map.put("logoutUrl", logoutUrl);
      mustache = mf.compile("results.html");
    } else {
      String loginUrl = userService.createLoginURL(String.format("/results?k=%s", key));
      map.put("loginUrl", loginUrl);
      mustache = mf.compile("results403.html");
    }
    response.setContentType("text/html;");
    mustache.execute(response.getWriter(), map).flush();
  }
}
