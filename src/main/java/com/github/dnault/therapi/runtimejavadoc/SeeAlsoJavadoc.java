package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a <tt>@see</tt> tag on a class or method.
 */
public class SeeAlsoJavadoc {
    private final Link link;

    public SeeAlsoJavadoc(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
