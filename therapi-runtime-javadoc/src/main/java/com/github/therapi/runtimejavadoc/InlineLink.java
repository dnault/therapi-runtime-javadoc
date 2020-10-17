package com.github.therapi.runtimejavadoc;

import java.util.Objects;

public class InlineLink extends CommentElement {
    private final Link link;

    public InlineLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public void visit(CommentVisitor visitor) {
        visitor.inlineLink(link);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineLink that = (InlineLink) o;
        return Objects.equals(link, that.link);
    }

    @Override
    public String toString() {
        return "link=" + link;
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
