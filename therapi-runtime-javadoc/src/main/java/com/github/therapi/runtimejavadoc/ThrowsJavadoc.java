package com.github.therapi.runtimejavadoc;

import java.util.Objects;


public class ThrowsJavadoc {
    private final String name;
    private final Comment comment;

    public ThrowsJavadoc(String name, Comment comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ThrowsJavadoc that = (ThrowsJavadoc) o;
        return Objects.equals( name, that.name ) &&
            Objects.equals( comment, that.comment );
    }

    @Override
    public int hashCode() {
        return Objects.hash( name, comment );
    }

    @Override
    public String toString() {
        return "ThrowsJavadoc{" +
            "name='" + name + '\'' +
            ", comment=" + comment +
            '}';
    }
}
