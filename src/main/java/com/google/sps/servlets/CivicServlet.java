package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.data.UrlRequest;

/** Servlet that gets civic information. */
@WebServlet("/civic")
public class CivicServlet extends HttpServlet {

  private static final String API_KEY = "API_KEY";  // Insert the API_KEY here for testing.
  private static final String API_PATH = "https://www.googleapis.com/civicinfo/v2/representatives";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String latitude = request.getParameter("lat");
    String longitude = request.getParameter("lng");
    Map<String, String> locationQueryParams = new HashMap<>();
    locationQueryParams.put("lat", latitude);
    locationQueryParams.put("lng", longitude);
    String locationUrl = String.format("%s://%s:%s/location",
        request.getScheme(), 
        request.getServerName(), 
        request.getServerPort());
    String address = "";
    try {
      // Location URL doesn't work on devserver, so isntead use hardcoded string.
      String locationJsonResult = UrlRequest.urlQuery(locationUrl, locationQueryParams);
      Gson gson = new Gson();
      Type mapType = new TypeToken<Map<String, String>>() {}.getType();
      Map<String, String> locationMap = gson.fromJson(locationJsonResult, mapType);
      address = String.format("%s %s, %s, %s %s", 
          locationMap.get("Street Number"),
          locationMap.get("Street Name"),
          locationMap.get("City"),
          locationMap.get("State"),
          locationMap.get("Zip Code"));
    } catch (FileNotFoundException fnfe) {
      System.out.println(fnfe);
      address = "1 LMU Dr, Los Angeles, California";
    } finally {
      Map<String, String> apiQueryParams = new HashMap<>();
      apiQueryParams.put("key", API_KEY);
      apiQueryParams.put("address", address);
      String jsonResult = UrlRequest.urlQuery(API_PATH, apiQueryParams);
      response.setContentType("application/json;");
      response.getWriter().println(jsonResult);
    }
  }
}