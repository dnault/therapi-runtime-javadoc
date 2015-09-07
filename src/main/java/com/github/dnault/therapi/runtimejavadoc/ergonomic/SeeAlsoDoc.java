package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a <tt>@see</tt> tag on a class or method.
 */
public class SeeAlsoDoc {
    private final Link link;

    public SeeAlsoDoc(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
