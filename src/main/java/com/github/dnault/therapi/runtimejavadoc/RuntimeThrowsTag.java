package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class RuntimeThrowsTag extends RuntimeTag {
    private final String exceptionName;
    private final String exceptionComment;
    private final String qualifiedTypeName;

    public RuntimeThrowsTag(@JsonProperty("name") String name,
                            @JsonProperty("text") String text,
                            @JsonProperty("inlineTags") ImmutableList<RuntimeTag> inlineTags,
                            @JsonProperty("exceptionName") String exceptionName,
                            @JsonProperty("exceptionComment") String exceptionComment,
                            @JsonProperty("qualifiedTypeName") String qualifiedTypeName) {
        super(name, text, inlineTags);
        this.exceptionName = exceptionName;
        this.exceptionComment = exceptionComment;
        this.qualifiedTypeName = qualifiedTypeName;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public String getExceptionComment() {
        return exceptionComment;
    }

    public String getQualifiedTypeName() {
        return qualifiedTypeName;
    }

    @Override
    public String getKind() {
        return "@throws";
    }
}
