package com.google.sps;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    /* Should not be static -- only for testing */
    private static Map<String, String> geoMap = new HashMap<>();

    // Coordinates hardcoded for testing purposes
    private static double latitude = 44.9778;
    private static double longitude = -93.2650;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static final String API_KEY = "AIzaSyCug6dlEbSzp_M56-cU-h8Elbk1yPFNvLM"; // Insert actual API key to test.

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
 
    }

    private static String getJson(URL url) throws IOException {
      Scanner jsonReader = new Scanner(url.openStream());
      String data = "";
      while(jsonReader.hasNext()) {
        data += jsonReader.nextLine();
      }
      jsonReader.close();
      return data;
    }

    private static void jsonToMap(String jsonString) {
      JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject();
      try {
        JsonArray resultsArray = responseObject.getAsJsonArray("results");
        JsonArray addressArray = resultsArray.get(0).getAsJsonObject().getAsJsonArray("address_components");
        JsonObject entry;
        String name, geoType;
        for (int i = 0; i < addressArray.size(); i++){
          entry = addressArray.get(i).getAsJsonObject();
          name = entry.getAsJsonPrimitive("long_name").getAsString();
          geoType = entry.getAsJsonArray("types").get(0).getAsString();
          if (geoType.equals("locality")) {
            geoMap.put("City", name);
          } else if (geoType.equals("administrative_area_level_1")) {
            geoMap.put("State", name);
          } else if (geoType.equals("postal_code")) {
            geoMap.put("Zip Code", name);
          } else if (geoType.equals("street_number")) {
            geoMap.put("Street Number", name);
          } else if (geoType.equals("route")) {
            geoMap.put("Street Name", name);
          }
        }
      } catch(Exception e ){
        e.printStackTrace();
      }
    }

    public static void doGetTest() {
      String queryParam = String.format("latlng=%1$f,%2$f", latitude, longitude);
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
          System.out.println(gson.toJson(geoMap));
          //response.getWriter().println(gson.toJson(articles));
        } else {
          System.out.println("Error: connection response code is: " + responseCode);
          System.out.println("Ensure coordinates are correct");
        }    
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

}