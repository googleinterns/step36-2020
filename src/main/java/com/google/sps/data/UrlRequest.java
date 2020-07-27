package com.google.sps.data;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/* Class conatining helper methods to make url requests */
public final class UrlRequest {
  /**
   * Returns the a JSON string with the API response given an URL path and the query parameters.
   */
  public static String urlQuery(String basePath, Map<String, String> params) throws IOException {
    String path = String.format("%s?%s", basePath, getParamsString(params));
    System.out.println("Path is " + path);
    URL url = new URL(path);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty("Content-Type", "application/json");
    int responseCode = connection.getResponseCode();
    if (responseCode != 200) {
      System.err.println("Error: connection response code is: " + responseCode);
    }
    InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
    StringBuilder contentBuilder = new StringBuilder();
    try (BufferedReader inputReader = new BufferedReader(inputStream)) {
      String inputLine;
      while ((inputLine = inputReader.readLine()) != null) {
        contentBuilder.append(inputLine);
      }
    } catch(IOException e) {
      System.out.println(e);
      return "";
    } finally {
      connection.disconnect();
    }
    return contentBuilder.toString();
  }

/**
  * MIT License
  * 
  * Copyright (c) 2017 Eugen Paraschiv
  * 
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  * 
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  */

  public static String getParamsString(Map<String, String> params) {
    List<String> paramsList = new ArrayList<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
        String encodedKey = encodeTerm(entry.getKey());
        String encodedValue = encodeTerm(entry.getValue());
        paramsList.add(String.format("%s=%s", encodedKey, encodedValue));
    }
    return String.join("&", paramsList);
  }

  public static String encodeTerm(String term) {
    try {
      return URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException uee) {
      System.out.println(uee);
      return "";
    }
  }
}
