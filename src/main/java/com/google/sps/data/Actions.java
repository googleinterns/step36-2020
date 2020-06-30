package com.google.sps.data;

import java.util.List;

/** Class containing diferent action classes*/
public final class Actions {

  // private final Map<String, > projects;

  // private Actions(ActionsBuilder builder) {
  //   this.projects = builder.projects;
  // }

  // public List<Project> getProjects() {
  //   return projects;
  // }

  // public static class ActionsBuilder {

  //   private final List<Project> projects;

  //   public ActionsBuilder(){

  //   }

  //   public ActionsBuilder projects(List<Project> projects) {
  //     this.projects = projects;
  //     return this;
  //   }

  //   public Actions build() {
  //     Actions actions = new Actions(this);
  //     return actions;
  //   }
  // }

  /** Class containing projects*/
  public static class Project {

    private final int id;
    private String title;
    private String summary;

    private Project (ProjectBuilder builder) {
      this.id = builder.id;
      this.title = builder.title;
      this.summary = builder.summary;
    }

    public int getId() {
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

      private final int id;
      private String title;
      private String summary;

      public ProjectBuilder(int id) {
        this.id = id;
      }

      public ProjectBuilder withTitle(String title) {
        this.title = title;
        return this.
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

}
