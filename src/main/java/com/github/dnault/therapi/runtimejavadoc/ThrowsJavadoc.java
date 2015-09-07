package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThrowsJavadoc {
    private final String name;
    private final Comment comment;

    public ThrowsJavadoc(@JsonProperty("name") String name,
                         @JsonProperty("comment") Comment comment) {
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
