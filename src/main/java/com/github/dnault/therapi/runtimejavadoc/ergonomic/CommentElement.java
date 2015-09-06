package com.github.dnault.therapi.runtimejavadoc.ergonomic;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommentText.class, name = "text"),
        @JsonSubTypes.Type(value = InlineLink.class, name = "see"),
        @JsonSubTypes.Type(value = InlineTag.class, name = "tag")})
public abstract class CommentElement {
}
