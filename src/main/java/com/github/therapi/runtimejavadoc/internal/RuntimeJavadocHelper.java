package com.github.therapi.runtimejavadoc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RuntimeJavadocHelper {
    public static <T> List<T> unmodifiableDefensiveCopy(List<T> list) {
        return list == null ? Collections.<T>emptyList() : unmodifiableList(new ArrayList<>(list));
    }

    public static <T> T first(T[] array) {
        return array == null || array.length == 0 ? null : array[0];
    }
}
