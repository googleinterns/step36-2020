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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** */
@RunWith(PowerMockRunner.class) 
@PrepareForTest({ ResultsServlet.class, DefaultMustacheFactory.class, UserServiceFactory.class})
public final class ResultsServletTest { 

  private final static ResultsServlet RESULTS_SERVLET = new ResultsServlet();
  private final static String LOGGED_IN_RESULT_TEMPLATE = "Key:%s-LogoutURL:%s";
  private final static String LOGGED_OUT_RESULT_TEMPLATE = "LoginUrl:%s";

  @Before
  public void setUpMustacheFactoryMock() throws Exception {
    DefaultMustacheFactory mf = new DefaultMustacheFactory();
    Mustache loggedInMustache = mf.compile("results.test");
    Mustache loggedOutMustache = mf.compile("results403.test");

    DefaultMustacheFactory mfMocked = PowerMockito.mock(DefaultMustacheFactory.class);
    PowerMockito.when(mfMocked.compile("results.html")).thenReturn(loggedInMustache);
    PowerMockito.when(mfMocked.compile("results403.html")).thenReturn(loggedOutMustache);

    PowerMockito.whenNew(DefaultMustacheFactory.class).withAnyArguments().thenReturn(mfMocked);
  }

  private void setUpUserServiceMock(boolean isUserLoggedIn, String url) {
    UserService userServiceMock = PowerMockito.mock(UserService.class);
    PowerMockito.when(userServiceMock.isUserLoggedIn()).thenReturn(isUserLoggedIn);
    if (isUserLoggedIn) {
      PowerMockito.when(userServiceMock.createLogoutURL("/")).thenReturn(url);
    } else {
      PowerMockito.when(userServiceMock.createLoginURL(Mockito.anyString())).thenReturn(url);
    }
    PowerMockito.mockStatic(UserServiceFactory.class);
    PowerMockito.when(UserServiceFactory.getUserService()).thenReturn(userServiceMock);
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
  public void testLoggedIn() throws IOException {
    String key = "value";
    String logoutURL = "url";
    boolean isUserLoggedIn = true;
    setUpUserServiceMock(isUserLoggedIn, logoutURL);
    String actual = mockServlet(key);
    String expected = String.format(LOGGED_IN_RESULT_TEMPLATE, key, logoutURL);
    Assert.assertTrue(actual.contains(expected));
  }

  @Test
  public void testLoggedOut() throws IOException {
    String key = "value";
    String loginURL = "url";
    boolean isUserLoggedIn = false;
    setUpUserServiceMock(isUserLoggedIn, loginURL);
    String actual = mockServlet(key);
    String expected = String.format(LOGGED_OUT_RESULT_TEMPLATE, loginURL);
    Assert.assertTrue(actual.contains(expected));
  }

  @Test
  public void testNonAsciiCharacters() throws IOException {
    String key = "ログアウトURL";
    String logoutURL = "url";
    boolean isUserLoggedIn = true;
    setUpUserServiceMock(isUserLoggedIn, logoutURL);
    String actual = mockServlet(key);
    String expected = String.format(LOGGED_IN_RESULT_TEMPLATE, key, logoutURL);
    Assert.assertTrue(actual.contains(expected));
  }
}
