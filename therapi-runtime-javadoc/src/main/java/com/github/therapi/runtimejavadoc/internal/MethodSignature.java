/*
 * Copyright 2022 David Nault and contributors
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

import com.github.therapi.runtimejavadoc.MethodJavadoc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.INIT;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;
import static java.util.Objects.requireNonNull;

public class MethodSignature {
    private final String name;
    private final List<String> paramTypes;

    private MethodSignature(String methodName, List<String> paramTypes) {
        this.name = requireNonNull(methodName);
        this.paramTypes = unmodifiableDefensiveCopy(paramTypes);
    }

    public static MethodSignature from(MethodJavadoc methodJavadoc) {
        return new MethodSignature(methodJavadoc.getName(), methodJavadoc.getParamTypes());
    }

    public static MethodSignature from(Executable executable) {
        List<String> paramTypes = Arrays.stream(executable.getParameterTypes())
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());
        String name;
        if (executable instanceof Method) {
            name = executable.getName();
        } else if (executable instanceof Constructor) {
            name = INIT;
        } else {
            throw new IllegalArgumentException("Unexpected executable type: " + executable.getClass());
        }

        return new MethodSignature(name, paramTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodSignature that = (MethodSignature) o;
        return Objects.equals(name, that.name) && Objects.equals(paramTypes, that.paramTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paramTypes);
    }

    @Override
    public String toString() {
        return name + "(" + String.join(",", paramTypes) + ")";
    }
}
