package com.github.therapi.runtimejavadoc;

public class ThrowsJavadoc {
  private final String name;
  private final Comment comment;

  public ThrowsJavadoc(String name, Comment comment) {
    this.name = name;
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public Comment getComment() {
    return comment;
  }
}
