package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParamJavadoc {
    private final String name;
    private final Comment comment;

    public ParamJavadoc(@JsonProperty("name") String name,
                        @JsonProperty("comment") Comment comment) {
        this.name = name;
        this.comment = comment;
    }

    /**
     * Returns the name of parameter
     * @return the name of parameter
     */
    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }
}
