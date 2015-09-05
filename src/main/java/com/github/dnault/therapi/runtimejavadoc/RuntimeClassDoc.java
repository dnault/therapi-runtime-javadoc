package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeClassDoc implements Serializable {
    private static final long serialVersionUID = 1;

    private final String qualifiedName;
    private final String comment;
    private final List<RuntimeFieldDoc> fields;
    private final List<RuntimeMethodDoc> methods;

    @JsonCreator
    public RuntimeClassDoc(@JsonProperty("qualifiedName") String qualifiedName,
                           @JsonProperty("comment")String comment,
                           @JsonProperty("fields") List<RuntimeFieldDoc> fields,
                           @JsonProperty("methods") List<RuntimeMethodDoc> methods) {
        this.qualifiedName = qualifiedName;
        this.comment = comment;
        this.fields = fields == null ? Collections.<RuntimeFieldDoc>emptyList()
                : Collections.unmodifiableList(new ArrayList<>(fields));
        this.methods = methods == null ? Collections.<RuntimeMethodDoc>emptyList()
                : Collections.unmodifiableList(new ArrayList<>(methods));
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getComment() {
        return comment;
    }

    public List<RuntimeFieldDoc> getFields() {
        return fields;
    }

    public List<RuntimeMethodDoc> getMethods() {
        return methods;
    }
}
