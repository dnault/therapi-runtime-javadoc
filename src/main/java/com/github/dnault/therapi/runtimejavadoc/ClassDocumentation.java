package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class ClassDocumentation {
    private final String name;
    private final Comment comment;
    private final List<SeeAlsoDoc> seeAlso;
    private final List<OtherDoc> other;
    private final List<MethodDocumentation> methods;

    public ClassDocumentation(@JsonProperty("name") String name,
                              @JsonProperty("comment") Comment comment,
                              @JsonProperty("other") List<OtherDoc> other,
                              @JsonProperty("seeAlso") List<SeeAlsoDoc> seeAlso,
                              @JsonProperty("methods") List<MethodDocumentation> methods) {
        this.name = name;
        this.comment = comment;
        this.other = unmodifiableDefensiveCopy(other);
        this.seeAlso = unmodifiableDefensiveCopy(seeAlso);
        this.methods = unmodifiableDefensiveCopy(methods);

    }

    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }

    public List<SeeAlsoDoc> getSeeAlso() {
        return seeAlso;
    }

    public List<OtherDoc> getOther() {
        return other;
    }

    public List<MethodDocumentation> getMethods() {
        return methods;
    }
}
