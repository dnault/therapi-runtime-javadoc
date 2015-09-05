package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class RuntimeMethodDoc extends RuntimeDoc implements Serializable {
    private static final long serialVersionUID = 1;

    private final String qualifiedName;
    private final String commentText;
    private final String signature;

    @JsonCreator
    public RuntimeMethodDoc(@JsonProperty("qualifiedName") String qualifiedName,
                            @JsonProperty("commentText") String commentText,
                            @JsonProperty("signature") String signature,
                            @JsonProperty("tags") List<RuntimeTag> tags) {
        super(tags);

        this.qualifiedName = qualifiedName;
        this.commentText = commentText;
        this.signature = signature;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getSignature() {
        return signature;
    }
}
