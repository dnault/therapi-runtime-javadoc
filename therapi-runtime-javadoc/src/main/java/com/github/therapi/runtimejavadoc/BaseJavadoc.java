package com.github.therapi.runtimejavadoc;

import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.requireNonNull;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public abstract class BaseJavadoc {

    private final String name;
    private final Comment comment;
    private final List<SeeAlsoJavadoc> seeAlso;
    private final List<OtherJavadoc> other;

    BaseJavadoc(String name, Comment comment, List<SeeAlsoJavadoc> seeAlso, List<OtherJavadoc> other) {
        this.name = requireNonNull(name);
        this.comment = Comment.nullToEmpty(comment);
        this.other = unmodifiableDefensiveCopy(other);
        this.seeAlso = unmodifiableDefensiveCopy(seeAlso);
    }

    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }

    public List<SeeAlsoJavadoc> getSeeAlso() {
        return seeAlso;
    }

    public List<OtherJavadoc> getOther() {
        return other;
    }

    /**
     * Returns {@code false} if this object has content, or {@code true}
     * if it represents absent Javadoc.
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * @deprecated use !{@link #isEmpty()} instead
     */
    @Deprecated
    public boolean isPresent() {
        return !isEmpty();
    }
}
