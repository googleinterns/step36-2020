package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.sps.data.Project;

/** Servlet that actions items.*/
@WebServlet("/actions")
public class ActionsServlet extends HttpServlet {

  private static final List<String> terms = Arrays.asList("Black Lives Matter", "COVID-19");
  private static final String API_KEY = "API_KEY";  // Insert the API_KEY here for testing.
  private static final String API_PATH = "https://api.globalgiving.org/api/public/services/search/projects/summary";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, List<Project>> jsonResultMap = new HashMap<>();
    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put("api_key", API_KEY);
    for (String term : terms) {
      String queryTerm = encodeTerm(term);
      queryParameters.replace("q", queryTerm);
      String jsonResult = urlQuery(API_PATH, queryParameters);
      List<Project> projectsList = extractProjectsList(jsonResult);
      jsonResultMap.put(term, projectsList);
    }
  
    Gson gson = new Gson();
    Map<String, Object> results = new HashMap<>();
    results.put("results", jsonResultMap);
    String jsonResultString = gson.toJson(results);
    response.setContentType("application/json;");
    response.getWriter().println(jsonResultString);
  }

  private String encodeTerm(String term) {
    try {
      return URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex.getCause());
    }
  }
  
  /**
   * Returns the a JSON string with the API response given an URL path and the query parameters.
   */
  private String urlQuery(String basePath, Map<String, String> parameters) throws IOException {
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
   * Extracts and returns a list of projects given a JSON string.
   */
  private List<Project> extractProjectsList(String originalJsonString) {
    Gson gson = new Gson();
    Map jsonMap = gson.fromJson(originalJsonString, Map.class);
    String[] jsonSections = {"search", "response", "projects"};
    for (String section : jsonSections) {
      jsonMap = (Map) jsonMap.get(section);
    }
    List projectsListJson = (List) jsonMap.get("project");
    List<Project> projectsList = new ArrayList<>();
    for (int i = 0; i < projectsListJson.size(); i++) {
      Map projectJson = (Map) projectsListJson.get(i);
      double id = (double) projectJson.get("id");
      String title = (String) projectJson.get("title");
      String summary = (String) projectJson.get("summary");
      Project project = new Project.ProjectBuilder(id)
                                  .withTitle(title)
                                  .withSummary(summary)
                                  .build();
      projectsList.add(project);
    }
    return projectsList;
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

  private String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
      StringBuilder result = new StringBuilder();

      for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
      }

      String resultString = result.toString();
      return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
  }
}
