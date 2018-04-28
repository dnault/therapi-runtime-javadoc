package com.github.therapi.runtimejavadoc;

import java.util.List;

public class FieldJavadoc extends BaseJavadoc {

    public FieldJavadoc(String name, Comment comment, List<OtherJavadoc> other, List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
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
