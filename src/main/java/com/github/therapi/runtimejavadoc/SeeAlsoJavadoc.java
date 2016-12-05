package com.github.therapi.runtimejavadoc;

/**
 * Represents a <tt>@see</tt> tag on a class or method.
 */
public class SeeAlsoJavadoc {
    private final Link link;

    public SeeAlsoJavadoc(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
