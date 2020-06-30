package com.google.sps.data;

/** Class containing projects*/
public final class Project {

  private final double id;
  private String title;
  private String summary;

  private Project (ProjectBuilder builder) {
    this.id = builder.id;
    this.title = builder.title;
    this.summary = builder.summary;
  }

  public double getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getSummary() {
    return summary;
  }

  /** Class used to build projects*/
  public static class ProjectBuilder {

    private final double id;
    private String title;
    private String summary;

    public ProjectBuilder(double id) {
      this.id = id;
    }

    public ProjectBuilder withTitle(String title) {
      this.title = title;
      return this;
    }

    public ProjectBuilder withSummary(String summary) {
      this.summary = summary;
      return this;
    }

    public Project build() {
      Project project = new Project(this);
      return project;
    }
  } 
}
