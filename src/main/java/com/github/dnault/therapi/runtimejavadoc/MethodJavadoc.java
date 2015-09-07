package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class MethodJavadoc {
    private final String name;
    private final String signature;
    private final Comment comment;
    private final List<ParamJavadoc> params;
    private final List<ThrowsJavadoc> exceptions;
    private final List<OtherJavadoc> other;
    private final Comment returns;
    private final List<SeeAlsoJavadoc> seeAlso;

    public MethodJavadoc(@JsonProperty("name") String name,
                         @JsonProperty("signature") String signature,
                         @JsonProperty("comment") Comment comment,
                         @JsonProperty("params") List<ParamJavadoc> params,
                         @JsonProperty("throws") List<ThrowsJavadoc> exceptions,
                         @JsonProperty("other") List<OtherJavadoc> other,
                         @JsonProperty("returns") Comment returns,
                         @JsonProperty("seeAlso") List<SeeAlsoJavadoc> seeAlso) {
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

    public List<ParamJavadoc> getParams() {
        return params;
    }

    public List<ThrowsJavadoc> getThrows() {
        return exceptions;
    }

    public List<OtherJavadoc> getOther() {
        return other;
    }

    public Comment getReturns() {
        return returns;
    }

    public List<SeeAlsoJavadoc> getSeeAlso() {
        return seeAlso;
    }
}
