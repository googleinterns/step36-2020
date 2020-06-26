package com.google.sps.data;

public class ArticleBuilder {
    public String title;
    public String link;

    public ArticleBuilder(String title) {
      this.title = title;
    }

    public ArticleBuilder withLink(String link) {
      this.link = link;
      return this;
    }
  
    public Article build() {
      return new Article(this.title, this.link);
    }
  }