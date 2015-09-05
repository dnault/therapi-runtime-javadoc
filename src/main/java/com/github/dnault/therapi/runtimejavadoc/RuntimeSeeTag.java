package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;

public class RuntimeSeeTag extends RuntimeTag implements Serializable {
    private static final long serialVersionUID = 1;

    private final String label;
    private final String referencedClassName;
    private final String referencedMemberName;

    public RuntimeSeeTag(@JsonProperty("name") String name,
                         @JsonProperty("kind") String kind,
                         @JsonProperty("text") String text,
                         @JsonProperty("label") String label,
                         @JsonProperty("referencedClassName") String referencedClassName,
                         @JsonProperty("referencedMemberName") String referencedMemberName) {
        super(name, kind, text, ImmutableList.<RuntimeTag>of());
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
