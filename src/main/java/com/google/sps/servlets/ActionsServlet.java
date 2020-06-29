package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Process;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;

/** Servlet that actions items.*/
@WebServlet("/actions")
public class ActionsServlet extends HttpServlet {

  private static final List<String> terms = Arrays.asList("Black Lives Matter", "COVID-19");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> jsonResultList = new ArrayList<>();
    String apiKey = "a752ec3e-9fcf-4500-9107-694351adc5ee"; //Insert the API_KEY here for testing
    String jsonBase = "\"%s\" : %s";
    for (String term : terms) {
      String queryTerm = encodeTerm(term);
      String jsonResult = curlProjects(apiKey, queryTerm);
      jsonResult = String.format(jsonBase, term, jsonResult);
      jsonResultList.add(jsonResult);
    }

    String jsonResultString = "{\"results\": {"+String.join(",", jsonResultList)+"}}";
    response.setContentType("application/json;");
    response.getWriter().println(jsonResultString);
  }

  private String encodeTerm(String term) throws RuntimeException {
    try {
        return URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex.getCause());
    }
  }
  /**
   * Returns the a json string with the API response given a queryTerm and the API key.
   */
  private String curlProjects(String apiKey, String queryTerm) throws IOException{
    String path = "https://api.globalgiving.org/api/public/services/search/projects/summary";
    String queryString = String.format("?api_key=%s&q=%s", apiKey, queryTerm);
    String[] curlCommand = { "curl", "-H", "Accept: application/json", "-H", "Content-Type: application/json", "-X", "GET", path+queryString};

    ProcessBuilder process = new ProcessBuilder(curlCommand);
    Process p = process.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    StringBuilder builder = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      builder.append(line);
      builder.append(System.getProperty("line.separator"));
    }
    return builder.toString();
  }
}
