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
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/** */
@RunWith(PowerMockRunner.class) 
@PrepareForTest({ DeleteKeywordsServlet.class, DatastoreServiceFactory.class})
public final class DeleteKeywordsServletTest { 

  private final static DeleteKeywordsServlet DELTE_KEYWORDS_SERVLET = new DeleteKeywordsServlet();

  private void setUpDatastoreMock(DatastoreService mockedDatastoreService) throws IOException {
    PowerMockito.mockStatic(DatastoreServiceFactory.class);
    PowerMockito.when(DatastoreServiceFactory.getDatastoreService()).thenReturn(mockedDatastoreService);
  }

  private void mockServlet(String key) throws IOException {
    HttpServletRequest mockedRequest = PowerMockito.mock(HttpServletRequest.class);
    HttpServletResponse mockedResponse = PowerMockito.mock(HttpServletResponse.class);

    PowerMockito.when(mockedRequest.getParameter("k")).thenReturn(key);
    DELTE_KEYWORDS_SERVLET.doPost(mockedRequest, mockedResponse);
    Mockito.verify(mockedRequest, Mockito.atLeast(1)).getParameter("k");
  }

  @Test
  public void testServlet() throws IOException {
    DatastoreService mockedDatastoreService = PowerMockito.mock(DatastoreService.class);
    setUpDatastoreMock(mockedDatastoreService);

    String expected = "ahpzfnN0ZXAyMDIwLXRlYW0zNi1jYXBzdG9uZXIUCxIHS2V5d29yZBiAgICYtMiZCgw";
    mockServlet(expected);

    ArgumentCaptor<Key> argumentCaptor = ArgumentCaptor.forClass(Key.class);
    Mockito.verify(mockedDatastoreService).delete(argumentCaptor.capture());

    String actual = KeyFactory.keyToString(argumentCaptor.getValue());
    Assert.assertEquals(expected, actual);
  }
}
