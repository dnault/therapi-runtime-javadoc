package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

/**
 * Comment text that may contain inline tags.
 */
public class Comment {
    private final List<CommentElement> elements;

    public Comment(@JsonProperty("elements") List<CommentElement> elements) {
        this.elements = unmodifiableDefensiveCopy(elements);
    }

    /**
     * Returns the elements this comment consists of.
     */
    public List<CommentElement> getElements() {
        return elements;
    }
}
