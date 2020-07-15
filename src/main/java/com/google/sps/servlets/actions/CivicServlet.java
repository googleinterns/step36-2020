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
    API_KEY = util.getAPIKey("civic");
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
      // Location URL doesn't work on devserver, so instead use hardcoded string.
      fnfe.printStackTrace();
      address = "1 LMU Dr, Los Angeles, California";
    } finally {
      Map<String, String> apiQueryParams = new HashMap<>();
      apiQueryParams.put("key", API_KEY);
      apiQueryParams.put("address", address);
      apiQueryParams.put("prettyPrint", "false");
      String jsonResult = UrlRequest.urlQuery(API_PATH, apiQueryParams);
      response.setContentType("application/json;");
      response.getWriter().println(jsonResult);
    }
  }
}
