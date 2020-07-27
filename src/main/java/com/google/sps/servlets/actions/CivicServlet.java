package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.GetAPIKeyUtil;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.data.UrlRequest;

/** Servlet that gets civic information. */
@WebServlet("/actions/civic")
public class CivicServlet extends HttpServlet {

  private static String API_KEY;
  private static final String API_PATH = "https://www.googleapis.com/civicinfo/v2/representatives";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    GetAPIKeyUtil util = new GetAPIKeyUtil();
    API_KEY = util.getAPIKey("google");
    String address = request.getParameter("address");
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("key", API_KEY);
    queryParams.put("address", address);
    queryParams.put("prettyPrint", "false");
    String jsonResult = UrlRequest.urlQuery(API_PATH, queryParams);
    System.out.println("json is: " + jsonResult);
    response.setContentType("application/json;");
    response.getWriter().println(jsonResult);    
  }
}
