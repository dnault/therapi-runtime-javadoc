package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class ThrowsDoc {
    private final String name;
    private final List<CommentElement> comment;

    public ThrowsDoc(@JsonProperty("name") String name,
                     @JsonProperty("comment") List<CommentElement> comment) {
        this.name = name;
        this.comment = unmodifiableDefensiveCopy(comment);
    }

    public String getName() {
        return name;
    }

    public List<CommentElement> getComment() {
        return comment;
    }
}
