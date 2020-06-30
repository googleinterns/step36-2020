package com.google.sps.data;

public class Article {
  private String title;
  private String link;
  private String date;

  private Article(Builder b) {
    this.title = b.title;
    this.link = b.link;
    this.date = b.date;
  }

  public static class Builder {
    private String title;
    private String link;
    private String date;

    public Builder(String title, String link) {
      this.title = title;
      this.link = link;
    }

    public Builder withDate(String date) {
      this.date = date;
      return this;
    }
  
    public Article build() {
      return new Article(this);
    }
  }
}
