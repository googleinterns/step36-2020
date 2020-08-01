package com.google.sps.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import com.google.sps.data.UrlRequest;

/** */
@RunWith(PowerMockRunner.class) 
@PrepareForTest({ UrlRequest.class, ProjectsServlet.class })
public final class ProjectsServletTest { 

  private final static ProjectsServlet PROJECTS_SERVLET = new ProjectsServlet();
  private final static String[] KEYWORD = {"keyword"};

  private void setUpUrlQueryMock(String response) throws IOException {
    PowerMockito.mockStatic(UrlRequest.class);
    PowerMockito.when(UrlRequest.urlQuery(Mockito.anyString(), Mockito.any(Map.class))).thenReturn(response);
  }

  private String mockServlet() throws IOException {
    HttpServletRequest mockedRequest = PowerMockito.mock(HttpServletRequest.class);
    HttpServletResponse mockedResponse = PowerMockito.mock(HttpServletResponse.class);

    PowerMockito.when(mockedRequest.getParameterValues("key")).thenReturn(KEYWORD);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    PowerMockito.when(mockedResponse.getWriter()).thenReturn(writer);

    PROJECTS_SERVLET.doGet(mockedRequest, mockedResponse);
    Mockito.verify(mockedRequest, Mockito.atLeast(1)).getParameterValues("key");
    writer.flush();
    return stringWriter.toString();
  }

  @Test
  public void testServlet() throws IOException {
    String apiResponse = "{\"search\":{\"response\":{\"projects\":{\"project\":[{"
                         + "\"id\": 1,"
                         + "\"title\": \"This is the title\","
                         + "\"summary\": \"This is the summary\","
                         + "\"projectLink\": \"This is the url\""
                         + "}]}}}}";
    setUpUrlQueryMock(apiResponse);
    String actual = mockServlet();
    String expected = "{\"results\":{\"keyword\":[{"
                         + "\"id\":1.0,"
                         + "\"title\":\"This is the title\","
                         + "\"summary\":\"This is the summary\","
                         + "\"url\":\"This is the url\""
                         + "}]}}";
    Assert.assertTrue(actual.contains(expected));
  }

  @Test
  public void testNonAsciiCharacters() throws IOException {
    String apiResponse = "{\"search\":{\"response\":{\"projects\":{\"project\":[{"
                         + "\"id\": 1,"
                         + "\"title\": \"यह शीर्षक है\","
                         + "\"summary\": \"यह सारांश है\","
                         + "\"projectLink\": \"यह url है\""
                         + "}]}}}}";
    setUpUrlQueryMock(apiResponse);
    String actual = mockServlet();
    String expected = "{\"results\":{\"keyword\":[{"
                         + "\"id\":1.0,"
                         + "\"title\":\"यह शीर्षक है\","
                         + "\"summary\":\"यह सारांश है\","
                         + "\"url\":\"यह url है\""
                         + "}]}}";
    Assert.assertTrue(actual.contains(expected));
  }

  @Test
  public void testEmptyApiResponse() throws IOException {
    String apiResponse = "";
    setUpUrlQueryMock(apiResponse);
    String actual = mockServlet();
    String expected = "{\"results\":{\"keyword\":[]}}";
    Assert.assertTrue(actual.contains(expected));
  }

  @Test
  public void testWrongApiResponse() throws IOException {
    String apiResponse = "This response is not in the correct json format";
    setUpUrlQueryMock(apiResponse);
    String actual = mockServlet();
    String expected = "{\"results\":{\"keyword\":[]}}";
    Assert.assertTrue(actual.contains(expected));
  }

}
