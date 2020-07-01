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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

@WebServlet("/location")
public class LocationServlet extends HttpServlet {
    /* Should not be static -- only for testing */
    private static ArrayList<String> geoTerms = new ArrayList<>();
    private static double latitude = 61.23;
    private static double longitude = 108.4;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static final String API_KEY = "AIzaSyCug6dlEbSzp_M56-cU-h8Elbk1yPFNvLM"; // Insert actual API key to test.

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
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
          System.out.println(json); // test
        } else {
          System.out.println("Error: connection response code is: " + responseCode);
        }    
      } catch(Exception e) {
        e.printStackTrace();
      }
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

    private static void jsonToTerms(String jsonString) {
      // TODO: get long name for each address component for first result
      JsonObject responseObject = new JsonParser().parse(jsonString).getAsJsonObject();
      JsonArray resultsArray = responseObject.getAsJsonArray("results");
      JsonArray addressArray = resultsArray.get(0).getAsJsonObject().getAsJsonArray("address_components");
      // for address object, iterate over each thing element in array and add long_name to geoTerms
      System.out.println(addressArray); // test
      String geoTerm;
      for (int i = 0; i < addressArray.size(); i++){
        geoTerm = addressArray.get(i).getAsJsonObject().getAsJsonPrimitive("long_name").getAsString();
        geoTerms.add(geoTerm);
        System.out.println(geoTerm); // test
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
          jsonToTerms(json);
        } else {
          System.out.println("Error: connection response code is: " + responseCode);
        }    
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

}