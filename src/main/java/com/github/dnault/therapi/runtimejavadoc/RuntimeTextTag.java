package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class RuntimeTextTag extends RuntimeTag {
    public RuntimeTextTag(@JsonProperty("text") String text) {
        super("Text", text, ImmutableList.<RuntimeTag>of());
    }

    @JsonIgnore
    public String getKind() {
        return "Text";
    }
}
