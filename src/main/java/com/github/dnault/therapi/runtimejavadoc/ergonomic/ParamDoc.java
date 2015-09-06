package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class ParamDoc {
    private final String name;
    private final List<CommentElement> comment;

    public ParamDoc(@JsonProperty("name") String name,
                    @JsonProperty("comment") List<CommentElement> comment) {
        this.name = name;
        this.comment = unmodifiableDefensiveCopy(comment);
    }

    /**
     * Returns the name of parameter
     */
    public String getName() {
        return name;
    }

    public List<CommentElement> getComment() {
        return comment;
    }
}
