package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.data.Project;
import com.google.sps.data.UrlRequest;

/** Servlet that gets actions items. */
@WebServlet("/actions")
public class ActionsServlet extends HttpServlet {

  private static final List<String> terms = Arrays.asList("Black Lives Matter", "COVID-19");
  private static final String API_KEY = "API_KEY";  // Insert the API_KEY here for testing.
  private static final String API_PATH = "https://api.globalgiving.org/api/public/services/search/projects";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, List<Project>> jsonResultMap = new HashMap<>();
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("api_key", API_KEY);
    for (String term : terms) {
      queryParams.replace("q", term);
      String jsonResult = UrlRequest.urlQuery(API_PATH, queryParams);
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

  /**
   * Extracts and returns a list of projects given a JSON string.
   */
  private List<Project> extractProjectsList(String originalJsonString) {
    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
    Map<String, Object> jsonMap = gson.fromJson(originalJsonString, mapType);
    String[] jsonSections = {"search", "response", "projects"};
    for (String section : jsonSections) {
      jsonMap = (Map<String, Object>) jsonMap.get(section);
    }
    List<Map<String, Object>> projectsListJson = (List<Map<String, Object>>) jsonMap.get("project");
    List<Project> projectsList = new ArrayList<>();
    for (int i = 0; i < projectsListJson.size(); i++) {
      Map<String, Object> projectJson = projectsListJson.get(i);
      Project project = getProjectFromMap(projectJson);
      projectsList.add(project);
    }
    return projectsList;
  }

  private Project getProjectFromMap(Map projectMap) {
    double id = (double) projectMap.get("id");
    String title = (String) projectMap.get("title");
    String summary = (String) projectMap.get("summary");
    String url  = (String) projectMap.get("projectLink");
    Project project = new Project.ProjectBuilder(id)
        .withTitle(title)
        .withSummary(summary)
        .build();
    return project;
  }
}
