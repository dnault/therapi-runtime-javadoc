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

package com.github.therapi.runtimejavadoc;

import com.github.therapi.runtimejavadoc.internal.MethodJavadocKey;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodJavadoc extends BaseJavadoc {
    public static final String INIT = "<init>";

    private final List<String> paramTypes;
    private final Map<String, ParamJavadoc> params;
    private final Map<String, ThrowsJavadoc> exceptions;
    private final Comment returns;

    public MethodJavadoc(String name,
                         List<String> paramTypes,
                         Comment comment,
                         List<ParamJavadoc> params,
                         List<ThrowsJavadoc> exceptions,
                         List<OtherJavadoc> other,
                         Comment returns,
                         List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);

        this.paramTypes = unmodifiableDefensiveCopy(paramTypes);
        this.returns = Comment.nullToEmpty(returns);

        Map<String, ParamJavadoc> paramJavadocMap = new LinkedHashMap<>();

        if (params != null) {
            params.forEach(paramJavadoc -> paramJavadocMap.put(paramJavadoc.getName(), paramJavadoc));
        }

        this.params = Collections.unmodifiableMap(paramJavadocMap);

        Map<String, ThrowsJavadoc> throwsJavadocMap = new LinkedHashMap<>();

        if (params != null) {
            exceptions.forEach(throwsJavadoc -> throwsJavadocMap.put(throwsJavadoc.getName(), throwsJavadoc));
        }

        this.exceptions = Collections.unmodifiableMap(throwsJavadocMap);
    }

    private MethodJavadoc(String name,
                          List<String> paramTypes,
                          Comment comment,
                          Map<String, ParamJavadoc> params,
                          Map<String, ThrowsJavadoc> exceptions,
                          List<OtherJavadoc> other,
                          Comment returns,
                          List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
        this.paramTypes = Collections.unmodifiableList(paramTypes);
        this.params = Collections.unmodifiableMap(params);
        this.exceptions = Collections.unmodifiableMap(exceptions);
        this.returns = returns;
    }

    public static MethodJavadoc createEmpty(Executable executable) {
        String name = executable instanceof Constructor ? INIT : executable.getName();
        List<String> paramTypes = Arrays.stream(executable.getParameterTypes())
                                        .map(Class::getCanonicalName)
                                        .collect(Collectors.toList());

        return new MethodJavadoc(name, paramTypes, null, (List<ParamJavadoc>) null, null, null, null, null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    public MethodJavadoc copyWithInheritance(MethodJavadoc superMethodJavadoc) {
        if (superMethodJavadoc.isEmpty()) {
            return this;
        }

        List<String> paramTypes = new ArrayList<>(this.paramTypes);
        if (paramTypes.isEmpty()) {
            paramTypes = superMethodJavadoc.paramTypes;
        }

        Comment comment = getComment();
        if (comment.getElements().isEmpty()) {
            comment = superMethodJavadoc.getComment();
        }

        Map<String, ParamJavadoc> params = new LinkedHashMap<>(this.params);
        superMethodJavadoc.params.forEach(params::putIfAbsent);

        Map<String, ThrowsJavadoc> exceptions = new LinkedHashMap<>(this.exceptions);
        superMethodJavadoc.exceptions.forEach(exceptions::putIfAbsent);

        Comment returns = this.returns;
        if (returns.getElements().isEmpty()) {
            returns = superMethodJavadoc.returns;
        }

        return new MethodJavadoc(getName(), paramTypes, comment, params, exceptions, getOther(), returns, getSeeAlso());
    }

    public boolean isConstructor() {
        return INIT.equals(getName());
    }

    public boolean fullyDescribes(Method method) {
        if (!method.getName().equals(getName()) || method.getParameterCount() != paramTypes.size()) {
            throw new IllegalArgumentException(String.format("Method `%s` does not match javadoc `%s`", method, this));
        }

        return !getComment().getElements().isEmpty()
               && !returns.getElements().isEmpty()
               && method.getParameterCount() == params.size()
               && Arrays.stream(method.getExceptionTypes())
                        .allMatch(exception -> exceptions.containsKey(exception.getSimpleName()));
    }

    public boolean matches(Executable executable) {
        if (executable instanceof Method) {
            return matches((Method) executable);
        } else if (executable instanceof Constructor) {
            return matches((Constructor<?>) executable);
        } else {
            throw new UnsupportedOperationException("Unknown executable type");
        }
    }

    private boolean matches(Method method) {
        return method.getName().equals(getName())
                && paramsMatch(method.getParameterTypes());
    }

    private boolean matches(Constructor<?> method) {
        return isConstructor()
                && paramsMatch(method.getParameterTypes());
    }

    private boolean paramsMatch(Class<?>[] paramTypes) {
        return getCanonicalNames(paramTypes).equals(this.paramTypes);
    }

    private static List<String> getCanonicalNames(Class<?>[] paramTypes) {
        List<String> methodParamsTypes = new ArrayList<>();
        for (Class<?> aClass : paramTypes) {
            methodParamsTypes.add(aClass.getCanonicalName());
        }
        return methodParamsTypes;
    }

    MethodJavadocKey toMethodJavadocKey() {
        return new MethodJavadocKey(getName(), paramTypes);
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public List<ParamJavadoc> getParams() {
        return Collections.unmodifiableList(new ArrayList<>(params.values()));
    }

    public List<ThrowsJavadoc> getThrows() {
        return Collections.unmodifiableList(new ArrayList<>(exceptions.values()));
    }

    public Comment getReturns() {
        return returns;
    }

    @Override
    public String toString() {
        return "MethodJavadoc{" +
                "name='" + getName() + '\'' +
                ", paramTypes='" + paramTypes + '\'' +
                ", comment=" + getComment() +
                ", params=" + params +
                ", exceptions=" + exceptions +
                ", other=" + getOther() +
                ", returns=" + returns +
                ", seeAlso=" + getSeeAlso() +
                '}';
    }
}
