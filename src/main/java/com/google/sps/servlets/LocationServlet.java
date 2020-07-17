package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.GetAPIKeyUtil;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.*;

@WebServlet("/location")
public class LocationServlet extends HttpServlet {

    private Map<String, String> geoMap = new HashMap<>();

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static String API_KEY;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      GetAPIKeyUtil util = new GetAPIKeyUtil();
      API_KEY = util.getAPIKey("google");
      String latitude = request.getParameter("lat");
      String longitude = request.getParameter("lng");
      String queryParam = String.format("latlng=%s,%s", latitude, longitude);
      String apiKeyParam = String.format("&key=%s", API_KEY);
      String fullPath = BASE_URL + queryParam + apiKeyParam;
      try {
        URL url = new URL(fullPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
          String json = getJson(url);
          jsonToMap(json);
          Gson gson = new Gson();
          response.getWriter().println(gson.toJson(geoMap));
        } else {
          System.out.println("Error: connection response code is: " + responseCode);
          System.out.println("Ensure coordinates are correct");
        }    
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    private String getJson(URL url) throws IOException {
      Scanner jsonReader = new Scanner(url.openStream());
      String data = "";
      while(jsonReader.hasNext()) {
        data += jsonReader.nextLine();
      }
      jsonReader.close();
      return data;
    }

   /**
    * Converts json response from Geolocation API to HashMap with important geographic characteristics.
    */
    private void jsonToMap(String jsonString) {
      JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject();
      try {
        JsonArray resultsArray = responseObject.getAsJsonArray("results");
        JsonArray addressArray = resultsArray.get(0).getAsJsonObject().getAsJsonArray("address_components");
        JsonObject entry;
        String name, geoType;
        // Extract useful location informatin from JSON array.
        for (int i = 0; i < addressArray.size(); i++){
          entry = addressArray.get(i).getAsJsonObject();
          name = entry.getAsJsonPrimitive("long_name").getAsString();
          geoType = entry.getAsJsonArray("types").get(0).getAsString();
          switch (geoType) {
            case "locality" :
              geoMap.put("City", name);
              break;
            case "administrative_area_level_1" :
              geoMap.put("State", name);
              break;
            case "postal_code" :
              geoMap.put("Zip Code", name);
              break;
            case "street_number" :
              geoMap.put("Street Number", name);
              break;
            case "route" :
              geoMap.put("Street Name", name);
              break;
            case "country":
              geoMap.put("Country", name);
              break;
            default:
              break;
          }
        }
      } catch(Exception e ){
        e.printStackTrace();
      }
    }

}
