package com.github.therapi.runtimejavadoc;

import java.util.Objects;

public class InlineTag extends CommentElement {
  private final String name;
  private final String value;

  public InlineTag(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineTag inlineTag = (InlineTag) o;
    return Objects.equals(name, inlineTag.name) && Objects.equals(value, inlineTag.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
