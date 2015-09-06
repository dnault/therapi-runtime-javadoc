package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

/**
 * Unstructured tag with a name and comment. May be a standard tag like "@author"
 * or a user-defined custom tag.
 */
public class OtherDoc {
    private final String name;
    private final List<CommentElement> comment;

    public OtherDoc(@JsonProperty("name") String name,
                    @JsonProperty("comment") List<CommentElement> value) {
        this.name = name;
        this.comment = unmodifiableDefensiveCopy(value);
    }

    /**
     * Returns the name of the tag (including the <tt>@</tt>)
     */
    public String getName() {
        return name;
    }

    public List<CommentElement> getComment() {
        return comment;
    }
}
