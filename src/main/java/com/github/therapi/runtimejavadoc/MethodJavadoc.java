package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

import java.util.List;

public class MethodJavadoc {
    private final String name;
    private final String signature;
    private final Comment comment;
    private final List<ParamJavadoc> params;
    private final List<ThrowsJavadoc> exceptions;
    private final List<OtherJavadoc> other;
    private final Comment returns;
    private final List<SeeAlsoJavadoc> seeAlso;

    public MethodJavadoc(String name,
                         String signature,
                         Comment comment,
                         List<ParamJavadoc> params,
                         List<ThrowsJavadoc> exceptions,
                         List<OtherJavadoc> other,
                         Comment returns,
                         List<SeeAlsoJavadoc> seeAlso) {
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

    @Override
    public String toString() {
        return "MethodJavadoc{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", comment=" + comment +
                ", params=" + params +
                ", exceptions=" + exceptions +
                ", other=" + other +
                ", returns=" + returns +
                ", seeAlso=" + seeAlso +
                '}';
    }
}
