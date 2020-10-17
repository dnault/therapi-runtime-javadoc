package com.github.therapi.runtimejavadoc;


public interface CommentVisitor {
    public void commentText( String value );

    public void inlineLink( Link link );

    public void inlineTag( String name, String value );

    public void inlineValue( Value value );
}
