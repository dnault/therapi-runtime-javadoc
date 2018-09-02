package com.github.therapi.runtimejavadoc;

import java.util.List;

public class FieldJavadoc extends BaseJavadoc {

    public FieldJavadoc(String name, Comment comment, List<OtherJavadoc> other, List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
    }

    public static FieldJavadoc createAbsent(String fieldName) {
        return new FieldJavadoc(fieldName, null, null, null) {
            @Override
            public boolean isPresent() {
                return false;
            }
        };
    }

    @Override
    public String toString() {
        return "FieldJavadoc{"
                + "name='" + getName() + '\''
                + ", comment=" + getComment()
                + ", other=" + getOther()
                + ", seeAlso=" + getSeeAlso()
                + '}';
    }
}
