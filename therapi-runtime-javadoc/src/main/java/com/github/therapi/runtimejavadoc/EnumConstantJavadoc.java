package com.github.therapi.runtimejavadoc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class EnumConstantJavadoc {
    private final String name;
    private final Comment comment;
    private final List<OtherJavadoc> other;
    private final List<SeeAlsoJavadoc> seeAlso;

    public EnumConstantJavadoc(String name,
                               Comment comment,
                               List<OtherJavadoc> other,
                               List<SeeAlsoJavadoc> seeAlso) {
        this.name = name;
        this.comment = comment;
        this.other = unmodifiableDefensiveCopy(other);
        this.seeAlso = unmodifiableDefensiveCopy(seeAlso);
    }

    public boolean matches(Enum enumConstant) {
        return enumConstant.name().equals(name);
    }

    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }

    public List<OtherJavadoc> getOther() {
        return other;
    }

    public List<SeeAlsoJavadoc> getSeeAlso() {
        return seeAlso;
    }

    @Override
    public String toString() {
        return "EnumConstantJavadoc{" +
                "name='" + name + '\'' +
                ", comment=" + comment +
                ", seeAlso=" + seeAlso +
                '}';
    }
}
