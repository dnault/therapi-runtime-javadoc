package com.github.therapi.runtimejavadoc;

public class InlineTag extends CommentElement {
    private final String name;
    private final String value;

    public InlineTag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
