package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeeAlsoDoc {
    private final Link link;

    public SeeAlsoDoc(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
