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

import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class ClassJavadoc extends BaseJavadoc {

    private final List<FieldJavadoc> fields;
    private final List<FieldJavadoc> enumConstants;
    private final List<MethodJavadoc> methods;
    private final List<MethodJavadoc> constructors;
    private final List<ParamJavadoc> recordComponents;

    public ClassJavadoc(String name, Comment comment, List<FieldJavadoc> fields, List<FieldJavadoc> enumConstants,
                        List<MethodJavadoc> methods, List<MethodJavadoc> constructors, List<OtherJavadoc> other, List<SeeAlsoJavadoc> seeAlso,
                        List<ParamJavadoc> recordComponents) {
        super(name, comment, seeAlso, other);
        this.fields = unmodifiableDefensiveCopy(fields);
        this.enumConstants = unmodifiableDefensiveCopy(enumConstants);
        this.methods = unmodifiableDefensiveCopy(methods);
        this.constructors = unmodifiableDefensiveCopy(constructors);
        this.recordComponents = unmodifiableDefensiveCopy(recordComponents);
    }

    public static ClassJavadoc createEmpty(String qualifiedClassName) {
        return new ClassJavadoc(qualifiedClassName, null, null, null, null, null, null, null, null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    public List<FieldJavadoc> getFields() {
        return fields;
    }

    public List<FieldJavadoc> getEnumConstants() {
        return enumConstants;
    }

    public List<MethodJavadoc> getMethods() {
        return methods;
    }

    public List<MethodJavadoc> getConstructors() {
        return constructors;
    }

    /**
     * If this class is a record, returns the "@param" tags from the canonical constructor.
     * Otherwise, returns an empty list.
     */
    public List<ParamJavadoc> getRecordComponents() {
        return recordComponents;
    }

    @Override
    public String toString() {
        return "ClassJavadoc{" +
                "name='" + getName() + '\'' +
                ", comment=" + getComment() +
                ", fields=" + fields +
                ", methods=" + methods +
                ", constructors=" + constructors +
                ", recordComponents=" + recordComponents +
                ", seeAlso=" + getSeeAlso() +
                ", other=" + getOther() +
                '}';
    }
}
