package com.google.sps.servlets;

public final class NewsTestUtil {
  
  public String textToString(String filepath) throws IOException {
    InputStreamReader inputStream = new InputStreamReader(filePath));
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader inputReader = new BufferedReader(inputStream)) {
      String inputLine;
      while ((inputLine = inputReader.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    } catch(IOException e) {
      System.err.println(e);
      return "";
    return stringBuilder.toString();
  }
}