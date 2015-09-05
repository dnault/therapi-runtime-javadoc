package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class RuntimeParamTag extends RuntimeTag {

    private final boolean typeParam;
    private final String paramName;
    private final String paramComment;

    public RuntimeParamTag(@JsonProperty("name") String name,
                           @JsonProperty("text") String text,
                           @JsonProperty("inlineTags") ImmutableList<RuntimeTag> inlineTags,
                           @JsonProperty("typeParam") boolean typeParam,
                           @JsonProperty("paramName") String paramName,
                           @JsonProperty("paramComment") String paramComment) {
        super(name, text, inlineTags);
        this.typeParam = typeParam;
        this.paramName = paramName;
        this.paramComment = paramComment;
    }

    @Override
    public String getKind() {
        return "@param";
    }

    public boolean isTypeParam() {
        return typeParam;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamComment() {
        return paramComment;
    }
}
