package com.github.therapi.runtimejavadoc.internal.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsedJavadoc {

  String description;

  List<BlockTag> blockTags = new ArrayList<>();

  @Override
  public String toString() {
    return "ParsedJavadoc{" +
        "description='" + description + '\'' +
        ", blockTags=" + blockTags +
        '}';
  }

  String getDescription() {
    return description;
  }

  List<BlockTag> getBlockTags() {
    return blockTags;
  }
}
