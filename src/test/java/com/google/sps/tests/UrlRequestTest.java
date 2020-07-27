package com.google.sps.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/** */
@RunWith(PowerMockRunner.class) 
@PrepareForTest({ URL.class, UrlRequest.class })
public final class UrlRequestTest {

  private final static String EMPTY_STRING = "";
  private final static Map<String, String> EMPTY_PARAMS = new HashMap<>();
  private final static String BASE_PATH = "http://www.testing.com";
  private final static String URL_PATH = String.format("%s?%s", BASE_PATH, UrlRequest.getParamsString(EMPTY_PARAMS));

  private static HttpURLConnection mockUrlConnection(String urlPath) throws Exception {
    URL mockedUrl = PowerMockito.mock(URL.class);
    HttpURLConnection mockedConnection = PowerMockito.mock(HttpURLConnection.class);

    PowerMockito.whenNew(URL.class).withArguments(urlPath).thenReturn(mockedUrl);
    PowerMockito.when(mockedUrl.openConnection()).thenReturn(mockedConnection);
    return mockedConnection;
  }

  @Test
  public void errorUrlQuery() throws Exception {
    int responseCode = 400;
  
    HttpURLConnection mockedConnection = mockUrlConnection(URL_PATH);
    PowerMockito.when(mockedConnection.getResponseCode()).thenReturn(responseCode);

    String actual = UrlRequest.urlQuery(BASE_PATH, EMPTY_PARAMS);
    String expected = String.format("{\"error\":%s}", responseCode);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void successfulURlQuery() throws Exception {
    int responseCode = 200;
    String content = "hello world";
    InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.toString()));

    HttpURLConnection mockedConnection = mockUrlConnection(URL_PATH);
    PowerMockito.when(mockedConnection.getResponseCode()).thenReturn(responseCode);
    PowerMockito.when(mockedConnection.getInputStream()).thenReturn(inputStream);

    String actual = UrlRequest.urlQuery(BASE_PATH, EMPTY_PARAMS);
    String expected = content;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void formatEmptyParamsMap() {
    String actual = UrlRequest.getParamsString(EMPTY_PARAMS);
    String expected = EMPTY_STRING;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void formatOneParam() {
    Map<String, String> params = new HashMap<>();
    params.put("hello", "world");
    String actual = UrlRequest.getParamsString(params);
    String expected = "hello=world";
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void formatMultipleParams() {
    Map<String, String> params = new HashMap<>();
    params.put("hello", "world");
    params.put("second", "param2");
    params.put("third", "param3");
    String response = UrlRequest.getParamsString(params);
    List<String> actual = Arrays.asList(response.split("&", 0));
    List<String> expected = Arrays.asList("hello=world", "second=param2", "third=param3");
    MatcherAssert.assertThat("Array equality without order", actual, containsInAnyOrder(expected.toArray()));
  }

  @Test
  public void encodeEmptyString() {
    String term = EMPTY_STRING;
    String actual = UrlRequest.encodeTerm(term);
    String expected = EMPTY_STRING;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void encodeWord() {
    String term = "hello";
    String actual = UrlRequest.encodeTerm(term);
    String expected = term;
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void encodeWordsAndSpace() {
    String term = "hello world";
    String actual = UrlRequest.encodeTerm(term);
    String expected = "hello+world";
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void encodeSpecialCharacters() {
    String term = "#$&+,/:;=?@";
    String actual = UrlRequest.encodeTerm(term);
    String expected = "%23%24%26%2B%2C%2F%3A%3B%3D%3F%40";
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void encodeNonASCIICharacter() {
    String term = "😂";
    String actual = UrlRequest.encodeTerm(term);
    String expected = "%F0%9F%98%82";
    Assert.assertEquals(expected, actual);
  }
}
