package com.google.sps.data;

public class Book {
  private String title;
  private String link;
  private String date;
  private String image;
  private String description;
  private String writer;

  private Book(Builder b) {
    this.title = b.title;
    this.link = b.link;
    this.date = b.date;
    this.image = b.image;
    this.description = b.description;
    this.writer = b.writer;
  }

  public static class Builder {
    private String title;
    private String link;
    private String date;
    private String image;
    private String description;
    private String writer;

    public Builder(String title, String link) {
      this.title = title;
      this.link = link;
    }

    public Builder withDate(String date) {
      this.date = date;
      return this;
    }

    public Builder withImage(String image) {
       this.image = image;
       return this;
    }

    public Builder withDescription(String description) {
       this.description = description;
       return this;
    }

    public Builder withWriter(String writer) {
       this.writer = writer;
       return this;
    }
  
    public Book build() {
      return new Book(this);
    }
  }
}
