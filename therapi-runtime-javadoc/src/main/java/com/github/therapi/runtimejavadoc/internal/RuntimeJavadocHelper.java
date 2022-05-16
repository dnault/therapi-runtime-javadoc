/*
 * Copyright 2015 David Nault and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.therapi.runtimejavadoc.internal;

import static com.github.therapi.runtimejavadoc.MethodJavadoc.INIT;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.unmodifiableList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RuntimeJavadocHelper {
    private RuntimeJavadocHelper() {
        throw new AssertionError("not instantiable");
    }

    public static <T> List<T> unmodifiableDefensiveCopy(List<T> list) {
        return list == null ? Collections.emptyList() : unmodifiableList(new ArrayList<>(list));
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
        for (Iterator<? extends CharSequence> i = items.iterator(); i.hasNext(); ) {
            result.append(i.next());
            if (i.hasNext()) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

    public static MethodJavadocKey executableToMethodJavadocKey(Executable executable) {
        List<String> paramTypes = Arrays.stream(executable.getParameterTypes())
                                     .map(Class::getCanonicalName)
                                     .collect(Collectors.toList());
        String name;
        if (executable instanceof Method) {
            name = executable.getName();
        } else if (executable instanceof Constructor) {
            name = INIT;
        } else {
            throw new UnsupportedOperationException("Unknown executable type");
        }

        return new MethodJavadocKey(name, paramTypes);
    }

    public static List<Class<?>> getAllTypeAncestors(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Class<?>> typeAncestors = new ArrayList<>();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            typeAncestors.add(superclass);
            typeAncestors.addAll(getAllTypeAncestors(superclass));
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            typeAncestors.addAll(Arrays.asList(interfaces));
            Arrays.stream(interfaces).map(RuntimeJavadocHelper::getAllTypeAncestors).forEach(typeAncestors::addAll);
        }
        return Collections.unmodifiableList(typeAncestors);
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
