package com.github.therapi.runtimejavadoc;


public class ParamJavadoc {
    private final String name;
    private final Comment comment;

    public ParamJavadoc(String name, Comment comment) {
        this.name = name;
        this.comment = comment;
    }

    /**
     * Returns the name of parameter
     *
     * @return the name of parameter
     */
    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "@" + name + " " + comment;
    }
}
