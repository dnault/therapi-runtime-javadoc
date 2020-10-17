package com.github.therapi.runtimejavadoc;

import java.util.Objects;

public class CommentText extends CommentElement {
    private final String value;

    public CommentText(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void visit(CommentVisitor visitor) {
        visitor.commentText(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentText that = (CommentText) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CommentText{" + "value='" + value + '\'' + '}';
    }
}
