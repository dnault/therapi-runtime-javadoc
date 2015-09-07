package com.github.dnault.therapi.runtimejavadoc.ergonomic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class RtMethodDoc {
    private final String name;
    private final String signature;
    private final List<CommentElement> comment;
    private final List<ParamDoc> params;
    private final List<ThrowsDoc> exceptions;
    private final List<OtherDoc> other;
    private final List<CommentElement> returns;
    private final List<SeeAlsoDoc> seeAlso;

    public RtMethodDoc(@JsonProperty("name") String name,
                       @JsonProperty("signature") String signature,
                       @JsonProperty("comment") List<CommentElement> comment,
                       @JsonProperty("params") List<ParamDoc> params,
                       @JsonProperty("throws") List<ThrowsDoc> exceptions,
                       @JsonProperty("other") List<OtherDoc> other,
                       @JsonProperty("returns") List<CommentElement> returns,
                       @JsonProperty("seeAlso") List<SeeAlsoDoc> seeAlso) {
        this.name = name;
        this.signature = signature;
        this.comment = unmodifiableDefensiveCopy(comment);
        this.params = unmodifiableDefensiveCopy(params);
        this.exceptions = unmodifiableDefensiveCopy(exceptions);
        this.other = unmodifiableDefensiveCopy(other);
        this.returns = unmodifiableDefensiveCopy(returns);
        this.seeAlso = unmodifiableDefensiveCopy(seeAlso);
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public List<CommentElement> getComment() {
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

    public List<CommentElement> getReturns() {
        return returns;
    }

    public List<SeeAlsoDoc> getSeeAlso() {
        return seeAlso;
    }
}
