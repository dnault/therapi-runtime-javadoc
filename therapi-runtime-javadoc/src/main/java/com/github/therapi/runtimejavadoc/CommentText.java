package com.github.therapi.runtimejavadoc;

public class CommentText extends CommentElement {
    private final String value;

    public CommentText(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
