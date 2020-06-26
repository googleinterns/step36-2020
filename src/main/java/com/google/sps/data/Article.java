package com.google.sps.data;

public class Article {
  private String title;
  private String link;

  private Article(Builder b) {
    this.title = b.title;
    this.link = b.link;
  }

  public static class Builder {
    private String title;
    private String link;

    public Builder(String title) {
      this.title = title;
    }

    public Builder withLink(String link) {
      this.link = link;
      return this;
    }
  
    public Article build() {
      return new Article(this);
    }
  }
}
