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

/** Servlet that shows results. */
@WebServlet("/results")
public class ResultsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String key = request.getParameter("k");
    Map<String, String> map = new HashMap<>();
    map.put("key", key);
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("results.html");
    response.setContentType("text/html;");
    mustache.execute(response.getWriter(), map).flush();
  }
}