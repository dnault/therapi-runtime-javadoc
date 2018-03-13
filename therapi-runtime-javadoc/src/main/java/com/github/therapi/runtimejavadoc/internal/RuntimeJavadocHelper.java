package com.github.therapi.runtimejavadoc.internal;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeJavadocHelper {
    private RuntimeJavadocHelper() {
        throw new AssertionError("not instantiable");
    }

    public static <T> List<T> unmodifiableDefensiveCopy(List<T> list) {
        return list == null ? Collections.<T>emptyList() : unmodifiableList(new ArrayList<>(list));
    }

    public static String javadocResourceSuffix() {
        return "__Javadoc.json";
    }

    public static String paramTypesFieldName() {
        return "paramTypes";
    }

    public static String methodsFieldName() {
        return "methods";
    }

    public static String classDocFieldName() {
        return "doc";
    }

    public static String methodDocFieldName() {
        return "doc";
    }

    public static String methodNameFieldName() {
        return "name";
    }
}
