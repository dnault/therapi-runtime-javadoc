package com.github.therapi.runtimejavadoc;


public interface CommentVisitor {
    void commentText(String value);

    void inlineLink(Link link);

    void inlineTag(String name, String value);

    void inlineValue(Value value);
}
