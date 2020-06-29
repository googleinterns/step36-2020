package com.google.sps.data;

public class Article {
  private String title;
  private String link;
  private String writer;
  private String date;

  private Article(Builder b) {
    this.title = b.title;
    this.link = b.link;
    this.title = b.title;
    this.link = b.link;
  }

  public static class Builder {
    private String title;
    private String link;
    private String writer;
    private String date;

    public Builder(String title, String link) {
      this.title = title;
      this.link = link;
    }

    public Builder withWriter(String writer) {
      this.writer = writer;
      return this;
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
