package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Unstructured tag with a name and comment. May be a standard tag like "@author"
 * or a user-defined custom tag.
 */
public class OtherJavadoc {
    private final String name;
    private final Comment comment;

    public OtherJavadoc(@JsonProperty("name") String name,
                        @JsonProperty("comment") Comment comment) {
        this.name = name;
        this.comment = comment;
    }

    /**
     * Returns the name of the tag (including the <tt>@</tt>)
     * @return the name of the tag (including the <tt>@</tt>)
     */
    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }
}
