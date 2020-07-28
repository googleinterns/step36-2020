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
@PrepareForTest({ UrlRequest.class, CivicServlet.class })
public final class CivicServletTest { 

  private final static CivicServlet CIVIC_SERVLET = new CivicServlet();

  private void setUpUrlQueryMock(String expectedResponse) throws IOException {
    PowerMockito.mockStatic(UrlRequest.class);
    PowerMockito.when(UrlRequest.urlQuery(Mockito.anyString(), Mockito.any(Map.class))).thenReturn(expectedResponse);
  }

  private String mockServlet() throws IOException {
    HttpServletRequest mockedRequest = PowerMockito.mock(HttpServletRequest.class);
    HttpServletResponse mockedResponse = PowerMockito.mock(HttpServletResponse.class);

    String address = "address";
    PowerMockito.when(mockedRequest.getParameter("address")).thenReturn(address);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    PowerMockito.when(mockedResponse.getWriter()).thenReturn(writer);

    CIVIC_SERVLET.doGet(mockedRequest, mockedResponse);

    Mockito.verify(mockedRequest, Mockito.atLeast(1)).getParameter("address");
    writer.flush();
    return stringWriter.toString();
  }

  @Test
  public void testServlet() throws IOException {
    String expectedResponse = "response";
    setUpUrlQueryMock(expectedResponse);
    String actual = mockServlet();
    Assert.assertTrue(actual.contains(expectedResponse));
  }

  @Test
  public void testNonASCIICharacters() throws IOException {
    String expectedResponse = "sí 👍";
    setUpUrlQueryMock(expectedResponse);
    String actual = mockServlet();
    Assert.assertTrue(actual.contains(expectedResponse));
  }
}
