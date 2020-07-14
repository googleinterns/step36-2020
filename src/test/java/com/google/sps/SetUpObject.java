package com.google.sps.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.*;
import org.mockito.Mockito;

public class SetUpObject {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter writer;

  public SetUpObject(HttpServletRequest request, HttpServletResponse response, StringWriter writer) {
    this.request = request;
    this.response = response;
    this.writer = writer;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public StringWriter getWriter() {
    return writer;
  }
}
