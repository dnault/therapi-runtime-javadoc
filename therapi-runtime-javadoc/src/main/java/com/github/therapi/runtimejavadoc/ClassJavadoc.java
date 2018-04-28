package com.github.therapi.runtimejavadoc;

import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class ClassJavadoc extends BaseJavadoc {

    private final List<FieldJavadoc> fields;
    private final List<FieldJavadoc> enumConstants;
    private final List<MethodJavadoc> methods;

    public ClassJavadoc(String name, Comment comment, List<FieldJavadoc> fields, List<FieldJavadoc> enumConstants,
            List<MethodJavadoc> methods, List<OtherJavadoc> other, List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
        this.fields = unmodifiableDefensiveCopy(fields);
        this.enumConstants = unmodifiableDefensiveCopy(enumConstants);
        this.methods = unmodifiableDefensiveCopy(methods);
    }

    public List<FieldJavadoc> getFields() {
        return fields;
    }

    public List<FieldJavadoc> getEnumConstants() {
        return enumConstants;
    }

    public List<MethodJavadoc> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return "ClassJavadoc{" +
                "name='" + getName() + '\'' +
                ", comment=" + getComment() +
                ", fields=" + fields +
                ", methods=" + methods +
                ", seeAlso=" + getSeeAlso() +
                ", other=" + getOther() +
                '}';
    }
}
