package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class RuntimeParamTag extends RuntimeTag {
    public RuntimeParamTag(@JsonProperty("name") String name,
                           @JsonProperty("kind") String kind,
                           @JsonProperty("text") String text,
                           @JsonProperty("inlineTags") ImmutableList<RuntimeTag> inlineTags) {
        super(name, kind, text, inlineTags);
    }


}
