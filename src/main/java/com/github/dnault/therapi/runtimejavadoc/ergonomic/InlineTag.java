package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InlineTag extends CommentElement {
    private final String name;
    private final String value;

    public InlineTag(@JsonProperty("name") String name,
                     @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
