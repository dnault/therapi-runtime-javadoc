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
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.executableToMethodJavadocKey;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.getAllTypeAncestors;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassJavadoc extends BaseJavadoc {
    private final Map<String, FieldJavadoc> fields;
    private final Map<String, FieldJavadoc> enumConstants;
    private final Map<MethodJavadocKey, MethodJavadoc> methods;
    private final Map<MethodJavadocKey, MethodJavadoc> constructors;
    private final Map<String, ParamJavadoc> recordComponents;

    public ClassJavadoc(String name, Comment comment, List<FieldJavadoc> fields, List<FieldJavadoc> enumConstants,
                        List<MethodJavadoc> methods, List<MethodJavadoc> constructors, List<OtherJavadoc> other,
                        List<SeeAlsoJavadoc> seeAlso, List<ParamJavadoc> recordComponents) {
        super(name, comment, seeAlso, other);

        Map<String, FieldJavadoc> fieldMap = new LinkedHashMap<>();
        if (fields != null) {
            fields.forEach(fieldJavadoc -> fieldMap.put(fieldJavadoc.getName(), fieldJavadoc));
        }
        this.fields = Collections.unmodifiableMap(fieldMap);

        Map<String, FieldJavadoc> enumMap = new LinkedHashMap<>();
        if (enumConstants != null) {
            enumConstants.forEach(fieldJavadoc -> enumMap.put(fieldJavadoc.getName(), fieldJavadoc));
        }
        this.enumConstants = Collections.unmodifiableMap(enumMap);

        Map<MethodJavadocKey, MethodJavadoc> methodsMap = new LinkedHashMap<>();
        if (methods != null) {
            methods.forEach(methodJavadoc -> methodsMap.put(methodJavadoc.toMethodJavadocKey(), methodJavadoc));
        }
        this.methods = Collections.unmodifiableMap(methodsMap);

        Map<MethodJavadocKey, MethodJavadoc> constructorsMap = new LinkedHashMap<>();
        if (constructors != null) {
            constructors.forEach(
                    methodJavadoc -> constructorsMap.put(methodJavadoc.toMethodJavadocKey(), methodJavadoc));
        }
        this.constructors = Collections.unmodifiableMap(constructorsMap);

        Map<String, ParamJavadoc> recordsMap = new LinkedHashMap<>();
        if (recordComponents != null) {
            recordComponents.forEach(paramJavadoc -> recordsMap.put(paramJavadoc.getName(), paramJavadoc));
        }
        this.recordComponents = Collections.unmodifiableMap(recordsMap);
    }

    private ClassJavadoc(String name, Comment comment, Map<String, FieldJavadoc> fields,
                         Map<String, FieldJavadoc> enumConstants, Map<MethodJavadocKey, MethodJavadoc> methods,
                         Map<MethodJavadocKey, MethodJavadoc> constructors, List<OtherJavadoc> other,
                         List<SeeAlsoJavadoc> seeAlso, Map<String, ParamJavadoc> recordComponents) {
        super(name, comment, seeAlso, other);
        this.fields = Collections.unmodifiableMap(fields);
        this.enumConstants = Collections.unmodifiableMap(enumConstants);
        this.methods = Collections.unmodifiableMap(methods);
        this.constructors = Collections.unmodifiableMap(constructors);
        this.recordComponents = Collections.unmodifiableMap(recordComponents);
    }

    public static ClassJavadoc createEmpty(String qualifiedClassName) {
        return new ClassJavadoc(qualifiedClassName, null, (List<FieldJavadoc>) null, null, null, null, null, null,
                                null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    ClassJavadoc createEnhancedClassJavadoc(Class<?> clazz) {
        if (!getName().equals(clazz.getCanonicalName())) {
            throw new IllegalArgumentException("Class `" + clazz.getCanonicalName() + "` does not match class doc for `" + getName() + "`");
        }

        if (isEmpty()) {
            return this;
        }

        Map<String, ClassJavadoc> classJavadocCache = new HashMap<>();

        classJavadocCache.put(clazz.getCanonicalName(), this);
        getAllTypeAncestors(clazz).forEach(cls -> classJavadocCache.put(cls.getCanonicalName(), RuntimeJavadoc.getSkinnyClassJavadoc(cls)));

        Map<MethodJavadocKey, MethodJavadoc> methodJavadocs = new LinkedHashMap<>();
        Arrays.stream(clazz.getDeclaredMethods())
              .forEach(method -> methodJavadocs.put(executableToMethodJavadocKey(method),
                                                    RuntimeJavadoc.getJavadoc(method, classJavadocCache)));

        return new ClassJavadoc(getName(), getComment(), fields, enumConstants, methodJavadocs, constructors,
                                getOther(), getSeeAlso(), recordComponents);
    }

    public List<FieldJavadoc> getFields() {
        return Collections.unmodifiableList(new ArrayList<>(fields.values()));
    }

    public List<FieldJavadoc> getEnumConstants() {
        return Collections.unmodifiableList(new ArrayList<>(enumConstants.values()));
    }

    public List<MethodJavadoc> getMethods() {
        return Collections.unmodifiableList(new ArrayList<>(methods.values()));
    }

    public List<MethodJavadoc> getConstructors() {
        return Collections.unmodifiableList(new ArrayList<>(constructors.values()));
    }

    /**
     * If this class is a record, returns the "@param" tags from the canonical constructor.
     * Otherwise, returns an empty list.
     *
     * @return a non-null (but possibly empty) list of param tags describing the record components,
     * in the order the tags appear in the Javadoc.
     */
    public List<ParamJavadoc> getRecordComponents() {
        return Collections.unmodifiableList(new ArrayList<>(recordComponents.values()));
    }

    FieldJavadoc findMatchingField(Field field) {
        return fields.getOrDefault(field.getName(), FieldJavadoc.createEmpty(field.getName()));
    }

    FieldJavadoc findMatchingEnumConstant(Enum<?> enumConstant) {
        return enumConstants.getOrDefault(enumConstant.name(), FieldJavadoc.createEmpty(enumConstant.name()));
    }

    MethodJavadoc findMatchingMethod(Method method) {
        MethodJavadocKey methodJavadocKey = executableToMethodJavadocKey(method);
        return methods.getOrDefault(methodJavadocKey, MethodJavadoc.createEmpty(method));
    }

    MethodJavadoc findMatchingConstructor(Constructor<?> constructor) {
        MethodJavadocKey methodJavadocKey = executableToMethodJavadocKey(constructor);
        return constructors.getOrDefault(methodJavadocKey, MethodJavadoc.createEmpty(constructor));
    }

    ParamJavadoc findRecordComponent(String recordComponent) {
        return recordComponents.getOrDefault(recordComponent, new ParamJavadoc(recordComponent, Comment.createEmpty()));
    }

    @Override
    public String toString() {
        return "ClassJavadoc{"
               + "name='" + getName() + '\''
               + ", comment=" + getComment()
               + ", fields=" + fields
               + ", methods=" + methods
               + ", constructors=" + constructors
               + ", recordComponents=" + recordComponents
               + ", seeAlso=" + getSeeAlso()
               + ", other=" + getOther() + '}';
    }
}
