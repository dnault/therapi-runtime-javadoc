package com.github.therapi.runtimejavadoc;

/**
 * Unstructured tag with a name and comment. May be a standard tag like "@author"
 * or a user-defined custom tag.
 */
public class OtherJavadoc {
  private final String name;
  private final Comment comment;

  public OtherJavadoc(String name, Comment comment) {
    this.name = name;
    this.comment = comment;
  }

  /**
   * Returns the name of the tag (including the <tt>@</tt>)
   *
   * @return the name of the tag (including the <tt>@</tt>)
   */
  public String getName() {
    return name;
  }

  public Comment getComment() {
    return comment;
  }

  @Override
  public String toString() {
    return "@" + name + " " + comment;
  }
}
