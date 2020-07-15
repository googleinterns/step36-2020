package com.google.sps.data;

import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;

public class GetAPIKeyUtil {

  public String getAPIKey(String apiName) throws IOException {
    System.out.println("in getAPIKey");
    Properties properties = new Properties();
    properties.load(GetAPIKeyUtil.class.getClassLoader().getResourceAsStream("project.properties"));  
    return properties.getProperty(apiName + "Key");
  }

}