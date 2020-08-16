package com.github.therapi.runtimejavadoc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RuntimeJavadocHelper {
    private RuntimeJavadocHelper() {
        throw new AssertionError("not instantiable");
    }

    public static <T> List<T> unmodifiableDefensiveCopy(List<T> list) {
        return list == null ? Collections.<T>emptyList() : unmodifiableList(new ArrayList<>(list));
    }

    public static <T> T requireNonNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> items) {
        requireNonNull(delimiter);
        StringBuilder result = new StringBuilder();
        for (Iterator<? extends CharSequence> i = items.iterator(); i.hasNext();) {
            result.append(i.next());
            if (i.hasNext()) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String javadocResourceSuffix() {
        return "__Javadoc.json";
    }

    public static String paramTypesFieldName() {
        return "paramTypes";
    }

    public static String fieldsFieldName() {
        return "fields";
    }

    public static String enumConstantsFieldName() {
        return "enumConstants";
    }

    public static String methodsFieldName() {
        return "methods";
    }

    public static String constructorsFieldName() {
        return "constructors";
    }

    public static String elementNameFieldName() {
        return "name";
    }

    public static String elementDocFieldName() {
        return "doc";
    }
}
