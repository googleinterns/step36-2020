package com.google.sps.data;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collection;
// import java.util.Collections;
// import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class UrlRequestTest {

  private final static String EMPTY_STRING = "";

  // Testing UrlRequest.urlQuery()
  // @Test

  // // Testing UrlRequest.getParamsString()
  // @Test

  // Testing UrlRequest.encodeTerm()

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