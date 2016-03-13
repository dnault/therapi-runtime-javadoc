package com.github.therapi.runtimejavadoc.internal;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeJavadocHelper {
    private RuntimeJavadocHelper() {
    }

    public static <T> List<T> unmodifiableDefensiveCopy(List<T> list) {
        return list == null ? Collections.<T>emptyList() : unmodifiableList(new ArrayList<>(list));
    }

    public static <T> T first(T[] array) {
        return array == null || array.length == 0 ? null : array[0];
    }


    public static String replaceLast(String s, String target, String replacement) {
        int targetIndex = s.lastIndexOf(target);
        if (targetIndex == -1) {
            return s;
        }
        return new StringBuilder(s).replace(targetIndex, targetIndex + target.length(), replacement).toString();
    }
}
