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

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.unmodifiableList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuntimeJavadocHelper {
    public static final String INIT = "<init>";

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

    public static List<Class<?>> getAllTypeAncestors(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        Map<String, Class<?>> typeAncestors = new LinkedHashMap<>();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            typeAncestors.put(superclass.getCanonicalName(), superclass);
            getAllTypeAncestors(superclass).forEach(cls -> typeAncestors.put(cls.getCanonicalName(), cls));
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> superType : interfaces) {
            typeAncestors.put(superType.getCanonicalName(), superType);
            getAllTypeAncestors(superType).forEach(cls -> typeAncestors.put(cls.getCanonicalName(), cls));
        }

        return Collections.unmodifiableList(new ArrayList<>(typeAncestors.values()));
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

    public static Method findBridgeMethod(Method method) {
        if (method.isBridge()) {
            return method;
        }

        Class<?> declaringClass = method.getDeclaringClass();
        for (Method bridgeMethod : declaringClass.getDeclaredMethods()) {
            if (bridgeMethod.isBridge()
                && method.getName().equals(bridgeMethod.getName())
                && parametersMatchWithErasure(method.getParameterTypes(), bridgeMethod.getParameterTypes())) {
                return bridgeMethod;
            }
        }

        return null;
    }

    private static boolean parametersMatchWithErasure(Class<?>[] parameterTypes, Class<?>[] erasureTypes) {
        if (parameterTypes.length != erasureTypes.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (!erasureTypes[i].isAssignableFrom(parameterTypes[i])) {
                return false;
            }
        }

        return true;
    }
}
