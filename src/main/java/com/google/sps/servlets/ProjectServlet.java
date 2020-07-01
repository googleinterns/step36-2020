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
import com.google.gson.Gson;
import com.google.sps.data.UrlRequest;

/** Servlet that shows projects.*/
@WebServlet("/project")
public class ProjectServlet extends HttpServlet {

  private static final String API_KEY = "API_KEY";  // Insert the API_KEY here for testing.
  private static final String API_PATH = "https://api.globalgiving.org/api/public/projectservice/projects/%s";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put("api_key", API_KEY);
    String idString = request.getParameter("id");
    String path = String.format(API_PATH, idString);
    String jsonString = UrlRequest.urlQuery(path, queryParameters);

    Gson gson = new Gson();
    Map jsonMap = gson.fromJson(jsonString, Map.class);

    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("project.html");
    response.setContentType("text/html;");
    mustache.execute(response.getWriter(), jsonMap).flush();
  }
}
