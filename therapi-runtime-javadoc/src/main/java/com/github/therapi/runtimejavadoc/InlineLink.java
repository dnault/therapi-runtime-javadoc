package com.github.therapi.runtimejavadoc;

public class InlineLink extends CommentElement {
    private final Link link;

    public InlineLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
