package com.github.therapi.runtimejavadoc;

import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class ClassJavadoc extends BaseJavadoc {

    private final List<FieldJavadoc> fields;
    private final List<FieldJavadoc> enumConstants;
    private final List<MethodJavadoc> methods;
    private final List<MethodJavadoc> constructors;

    public ClassJavadoc(String name, Comment comment, List<FieldJavadoc> fields, List<FieldJavadoc> enumConstants,
            List<MethodJavadoc> methods, List<MethodJavadoc> constructors, List<OtherJavadoc> other, List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
        this.fields = unmodifiableDefensiveCopy(fields);
        this.enumConstants = unmodifiableDefensiveCopy(enumConstants);
        this.methods = unmodifiableDefensiveCopy(methods);
        this.constructors = unmodifiableDefensiveCopy(constructors);
    }

    public static ClassJavadoc createEmpty(String qualifiedClassName) {
        return new ClassJavadoc(qualifiedClassName, null, null, null, null, null, null, null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
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

    public List<MethodJavadoc> getConstructors() {
        return constructors;
    }

    public MethodJavadoc getMethod(String methodName) {
        for ( MethodJavadoc m : methods ) {
            if ( m.getName().equals(methodName) ) {
                return m;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "ClassJavadoc{" +
                "name='" + getName() + '\'' +
                ", comment=" + getComment() +
                ", fields=" + fields +
                ", methods=" + methods +
                ", constructors=" + constructors +
                ", seeAlso=" + getSeeAlso() +
                ", other=" + getOther() +
                '}';
    }
}
