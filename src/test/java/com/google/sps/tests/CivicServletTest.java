package com.google.sps.servlets;

// import java.io.ByteArrayInputStream;
// import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
// import java.nio.charset.StandardCharsets;
import java.util.Map;
// import java.util.HashMap;
// import java.util.Arrays;
// import java.util.List;
// import java.net.URL;
// import java.net.URLConnection;
// import java.net.HttpURLConnection;
// import java.net.MalformedURLException;
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
// import org.hamcrest.MatcherAssert;
import com.google.sps.data.UrlRequest;
// import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/** */
@RunWith(PowerMockRunner.class) 
@PrepareForTest({ UrlRequest.class, CivicServlet.class })
public final class CivicServletTest { 
  
  private final static String EXPECTED_RESPONSE = "response";

  @BeforeClass
  public static void setUpUrlQueryMock() throws IOException {
    PowerMockito.mockStatic(UrlRequest.class);
    PowerMockito.when(UrlRequest.urlQuery(Mockito.anyString(), Mockito.any(Map.class))).thenReturn(EXPECTED_RESPONSE);
  }

  @Test
  public void testServlet() throws IOException {
    HttpServletRequest mockedRequest = PowerMockito.mock(HttpServletRequest.class);
    HttpServletResponse mockedResponse = PowerMockito.mock(HttpServletResponse.class);

    String address = "address";
    PowerMockito.when(mockedRequest.getParameter("address")).thenReturn(address);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    PowerMockito.when(mockedResponse.getWriter()).thenReturn(writer);

    new CivicServlet().doGet(mockedRequest, mockedResponse);

    Mockito.verify(mockedRequest, Mockito.atLeast(1)).getParameter("address");
    writer.flush();
    String actual = stringWriter.toString();
    Assert.assertTrue(actual.contains(EXPECTED_RESPONSE));
  }
}