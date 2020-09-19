package com.github.therapi.runtimejavadoc;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

/**
 * Comment text that may contain inline tags.
 */
public class Comment implements Iterable<CommentElement> {
    private static final Comment EMPTY = new Comment(Collections.<CommentElement>emptyList());

    public static Comment createEmpty() {
        return EMPTY;
    }

    public static Comment nullToEmpty(Comment c) {
        return c == null ? EMPTY : c;
    }

    private final List<CommentElement> elements;

    public Comment(List<CommentElement> elements) {
        this.elements = unmodifiableDefensiveCopy(elements);
    }

    /**
     * Returns the elements this comment consists of.
     *
     * @return the elements this comment consists of.
     */
    public List<CommentElement> getElements() {
        return elements;
    }

    @Override
    public Iterator<CommentElement> iterator() {
        return elements.iterator();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        Comment that = (Comment) o;
        return Objects.equals( elements, that.elements );
    }

    @Override
    public int hashCode() {
        return Objects.hash( elements );
    }

    @Override
    public String toString() {
        return new CommentFormatter().format(this);
    }
}
