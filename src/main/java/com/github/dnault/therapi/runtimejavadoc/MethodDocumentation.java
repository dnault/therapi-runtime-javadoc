package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class MethodDocumentation {
    private final String name;
    private final String signature;
    private final Comment comment;
    private final List<ParamDoc> params;
    private final List<ThrowsDoc> exceptions;
    private final List<OtherDoc> other;
    private final Comment returns;
    private final List<SeeAlsoDoc> seeAlso;

    public MethodDocumentation(@JsonProperty("name") String name,
                               @JsonProperty("signature") String signature,
                               @JsonProperty("comment") Comment comment,
                               @JsonProperty("params") List<ParamDoc> params,
                               @JsonProperty("throws") List<ThrowsDoc> exceptions,
                               @JsonProperty("other") List<OtherDoc> other,
                               @JsonProperty("returns") Comment returns,
                               @JsonProperty("seeAlso") List<SeeAlsoDoc> seeAlso) {
        this.name = name;
        this.signature = signature;
        this.comment = comment;
        this.params = unmodifiableDefensiveCopy(params);
        this.exceptions = unmodifiableDefensiveCopy(exceptions);
        this.other = unmodifiableDefensiveCopy(other);
        this.returns = returns;
        this.seeAlso = unmodifiableDefensiveCopy(seeAlso);
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public Comment getComment() {
        return comment;
    }

    public List<ParamDoc> getParams() {
        return params;
    }

    public List<ThrowsDoc> getThrows() {
        return exceptions;
    }

    public List<OtherDoc> getOther() {
        return other;
    }

    public Comment getReturns() {
        return returns;
    }

    public List<SeeAlsoDoc> getSeeAlso() {
        return seeAlso;
    }
}
