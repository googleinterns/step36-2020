package com.google.sps.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.DefaultMustacheFactory;

/** */
@RunWith(PowerMockRunner.class) 
@PrepareForTest({ ResultsServlet.class, DefaultMustacheFactory.class})
public final class ResultsServletTest { 

  private final static ResultsServlet RESULTS_SERVLET = new ResultsServlet();

  @Before
  public void setUpMustacheFactoryMock() throws Exception {
    DefaultMustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("results.test");

    DefaultMustacheFactory mfMocked = PowerMockito.mock(DefaultMustacheFactory.class);
    PowerMockito.when(mfMocked.compile("results.html")).thenReturn(mustache);

    PowerMockito.whenNew(DefaultMustacheFactory.class).withAnyArguments().thenReturn(mfMocked);
  }

  private String mockServlet(String keyValue) throws IOException {
    HttpServletRequest mockedRequest = PowerMockito.mock(HttpServletRequest.class);
    HttpServletResponse mockedResponse = PowerMockito.mock(HttpServletResponse.class);

    PowerMockito.when(mockedRequest.getParameter("k")).thenReturn(keyValue);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    PowerMockito.when(mockedResponse.getWriter()).thenReturn(writer);

    RESULTS_SERVLET.doGet(mockedRequest, mockedResponse);

    Mockito.verify(mockedRequest, Mockito.atLeast(1)).getParameter("k");
    writer.flush();
    return stringWriter.toString();
  }

  @Test
  public void testServlet() throws IOException {
    String expected = "value";
    String actual = mockServlet(expected);
    Assert.assertTrue(actual.contains(expected));
  }

  @Test
  public void testNonAsciiCharacters() throws IOException {
    String expected = "मान";
    String actual = mockServlet(expected);
    Assert.assertTrue(actual.contains(expected));
  }
}
