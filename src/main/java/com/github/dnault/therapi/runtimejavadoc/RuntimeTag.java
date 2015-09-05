package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;


@JsonInclude(NON_EMPTY)
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@kind")
@JsonTypeName("custom")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuntimeSeeTag.class, name = "see"),
        @JsonSubTypes.Type(value = RuntimeThrowsTag.class, name = "throws"),
        @JsonSubTypes.Type(value = RuntimeParamTag.class, name = "param"),
        @JsonSubTypes.Type(value = RuntimeTextTag.class, name = "text")})
public class RuntimeTag implements Serializable {
    private static final long serialVersionUID = 1;

    private final String name;
    private final String text;
    private final ImmutableList<RuntimeTag> inlineTags;

    public RuntimeTag(@JsonProperty("name") String name,
                      @JsonProperty("text") String text,
                      @JsonProperty("inlineTags") ImmutableList<RuntimeTag> inlineTags) {
        this.name = name;
        this.text = text;
        this.inlineTags = inlineTags;
    }

    @JsonIgnore
    public String getKind() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public ImmutableList<RuntimeTag> getInlineTags() {
        return inlineTags;
    }
}
