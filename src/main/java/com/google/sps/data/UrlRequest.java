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

/* Class conatining helper methods to make url requests */
public final class UrlRequest {
  /**
   * Returns the a json string with the API response given an URL path and the query parameters.
   */
  public static String urlQuery(String basePath, Map<String, String> parameters) throws IOException {
    String path = String.format("%s?%s", basePath, getParamsString(parameters));
    URL url = new URL(path);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty("Content-Type", "application/json");
    int responseCode = connection.getResponseCode();
    InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
    StringBuilder contentBuilder = new StringBuilder();
    try (BufferedReader inputReader = new BufferedReader(inputStream)) {
      String inputLine;
      while ((inputLine = inputReader.readLine()) != null) {
        contentBuilder.append(inputLine);
      }
    }
    connection.disconnect();
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

  public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
      StringBuilder result = new StringBuilder();
      for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
          result.append("&");
      }
      String resultString = result.toString();
      return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
  }
}
