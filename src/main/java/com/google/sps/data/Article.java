package com.google.sps.data;

public class Article {

  private String title;
  private String link;

  public Article(String title, String link) {
    this.title = title;
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public String getLink() {
    return link;
  }  

}
