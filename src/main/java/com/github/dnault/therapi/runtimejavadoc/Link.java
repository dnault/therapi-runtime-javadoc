package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {
    private final String label;
    private final String referencedClassName;
    private final String referencedMemberName;

    public Link(@JsonProperty("label") String label,
                @JsonProperty("referencedClassName") String referencedClassName,
                @JsonProperty("referencedMemberName") String referencedMemberName) {
        this.label = label;
        this.referencedClassName = referencedClassName;
        this.referencedMemberName = referencedMemberName;
    }

    public String getLabel() {
        return label;
    }

    public String getReferencedClassName() {
        return referencedClassName;
    }

    public String getReferencedMemberName() {
        return referencedMemberName;
    }
}
