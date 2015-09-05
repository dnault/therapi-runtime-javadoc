package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonTypeName("Generic")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuntimeSeeTag.class, name = "See"),
        @JsonSubTypes.Type(value = RuntimeParamTag.class, name = "Param")})
public class RuntimeTag implements Serializable {
    private static final long serialVersionUID = 1;

    private final String name;
    private final String kind;
    private final String text;
    private final ImmutableList<RuntimeTag> inlineTags;


    public RuntimeTag(@JsonProperty("name") String name,
                      @JsonProperty("kind") String kind,
                      @JsonProperty("text") String text,
                      @JsonProperty("inlineTags") ImmutableList<RuntimeTag> inlineTags) {
        this.name = name;
        this.kind = kind;
        this.text = text;
        this.inlineTags = inlineTags;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public String getText() {
        return text;
    }

    public ImmutableList<RuntimeTag> getInlineTags() {
        return inlineTags;
    }
}
