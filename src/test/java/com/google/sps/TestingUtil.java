package com.google.sps.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.*;
import org.mockito.Mockito;
import java.io.PrintWriter;
import java.io.IOException;

public class TestingUtil { 

 /**
  * @return SetUpObject prepared for mocking a get request from a servlet.
  */
  public static SetUpObject mockGet(Map<String, String[]> parametersMap) throws IOException{
    StringWriter testWriter = new StringWriter();
    HttpServletRequest testRequest = Mockito.mock(HttpServletRequest.class);
    Mockito.when(testRequest.getMethod()).thenReturn("GET");
    for (String key : parametersMap.keySet()) {
      Mockito.when(testRequest.getParameterValues(key)).thenReturn(parametersMap.get(key));
    }
    HttpServletResponse testResponse = Mockito.mock(HttpServletResponse.class);
    Mockito.when(testResponse.getWriter()).thenReturn(new PrintWriter(testWriter));
    return new SetUpObject(testRequest, testResponse, testWriter);
  }

 /**
  * Given String representation of map that looks like {"xxxx" : {...}}, returns {...}
  * @return substring of input representing the inner map
  */
  public static String getInnerMap(String mapString) {
    int colonIndex = mapString.indexOf(':');
    return mapString.substring(colonIndex + 1, mapString.length() - 2);
  }
}
