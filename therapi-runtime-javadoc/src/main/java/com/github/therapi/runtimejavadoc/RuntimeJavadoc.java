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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.github.therapi.runtimejavadoc.internal.JsonJavadocReader;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.javadocResourceSuffix;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Allows access to Javadoc elements at runtime for code that was compiled using the
 * {@code therapi-runtime-javadoc-scribe} annotation processor.
 */
public class RuntimeJavadoc {

    private RuntimeJavadoc() {
        throw new AssertionError("not instantiable");
    }

    /**
     * Gets the Javadoc of the given class.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param clazz the class whose Javadoc you want to retrieve
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(Class<?> clazz) {
        return getSkinnyClassJavadoc(clazz.getName(), clazz).createEnhancedClassJavadoc(clazz);
    }

    /**
     * Gets the Javadoc of the given class.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param qualifiedClassName the fully qualified name of the class whose Javadoc you want to retrieve
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(String qualifiedClassName) {
        return getJavadoc(qualifiedClassName, RuntimeJavadoc.class);
    }

    /**
     * Gets the Javadoc of the given class, using the given {@link ClassLoader} to find the Javadoc resource.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param qualifiedClassName the fully qualified name of the class whose Javadoc you want to retrieve
     * @param loader        the class loader to use to find the Javadoc resource file
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(String qualifiedClassName, ClassLoader loader) {
        try {
            return getSkinnyClassJavadoc(qualifiedClassName, loader)
                    .createEnhancedClassJavadoc(loader.loadClass(qualifiedClassName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the Javadoc of the given class, using the given {@link Class} object to load the Javadoc resource.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param qualifiedClassName the fully qualified name of the class whose Javadoc you want to retrieve
     * @param loader             the class object to use to find the Javadoc resource file
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(String qualifiedClassName, Class<?> loader) {
        try {
            return getSkinnyClassJavadoc(qualifiedClassName, loader)
                    .createEnhancedClassJavadoc(loader.getClassLoader().loadClass(qualifiedClassName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static ClassJavadoc getSkinnyClassJavadoc(Class<?> clazz) {
        return getSkinnyClassJavadoc(clazz.getName(), clazz);
    }

    private static ClassJavadoc getSkinnyClassJavadoc(String qualifiedClassName, ClassLoader loader) {
        final String resourceName = getResourceName(qualifiedClassName);
        try (InputStream is = loader.getResourceAsStream("/" + resourceName)) {
            return parseJavadocResource(qualifiedClassName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClassJavadoc getSkinnyClassJavadoc(String qualifiedClassName, Class<?> loader) {
        final String resourceName = getResourceName(qualifiedClassName);
        try (InputStream is = loader.getResourceAsStream("/" + resourceName)) {
            return parseJavadocResource(qualifiedClassName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getResourceName(String qualifiedClassName) {
        return qualifiedClassName.replace(".", "/") + javadocResourceSuffix();
    }

    private static ClassJavadoc parseJavadocResource(String qualifiedClassName, InputStream is) throws IOException {
        if (is == null) {
            return ClassJavadoc.createEmpty(qualifiedClassName);
        }

        try (InputStreamReader r = new InputStreamReader(is, UTF_8)) {
            JsonObject json = Json.parse(r).asObject();
            return JsonJavadocReader.readClassJavadoc(qualifiedClassName, json);
        }
    }

    /**
     * Gets the Javadoc of the given method.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the method signature
     * with the correct documentation. If the client code's purpose is to loop through all methods doc, prefer using
     * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getMethods()} on the
     * returned class doc to retrieve method docs.
     *
     * @param method the method whose Javadoc you want to retrieve
     * @return the given method's Javadoc
     */
    public static MethodJavadoc getJavadoc(Method method) {
        return getJavadoc(method, Collections.emptyMap());
    }

    static MethodJavadoc getJavadoc(Method method, Map<String, ClassJavadoc> classJavadocCache) {
        Class<?> declaringClass = method.getDeclaringClass();
        ClassJavadoc classJavadoc = classJavadocCache.get(declaringClass.getCanonicalName());
        if (classJavadoc == null) {
            classJavadoc = getSkinnyClassJavadoc(declaringClass);
        }

        MethodJavadoc methodJavadoc = classJavadoc.findMatchingMethod(method);
        if (methodJavadoc.fullyDescribes(method)) {
            return methodJavadoc;
        }

        MethodJavadoc overriddenMethodJavadoc = findOverriddenMethodJavadoc(method, declaringClass, classJavadocCache);
        methodJavadoc = methodJavadoc.copyWithInheritance(overriddenMethodJavadoc);

        if (methodJavadoc.fullyDescribes(method)) {
            return methodJavadoc;
        }

        Method bridgeMethod = findBridgeMethod(method);
        if (bridgeMethod != null && method != bridgeMethod) {
            MethodJavadoc bridgeMethodJavadoc = findOverriddenMethodJavadoc(bridgeMethod, declaringClass, classJavadocCache);
            methodJavadoc = methodJavadoc.copyWithInheritance(bridgeMethodJavadoc);
        }

        return methodJavadoc;
    }

    private static MethodJavadoc findOverriddenMethodJavadoc(Method method, Class<?> searchedClass, Map<String, ClassJavadoc> classJavadocCache) {
        List<Class<?>> superTypes = new ArrayList<>();
        Class<?> superClass = searchedClass.getSuperclass();
        if (superClass != null) {
            superTypes.add(superClass);
        }

        superTypes.addAll(Arrays.asList(searchedClass.getInterfaces()));

        for (Class<?> superType : superTypes) {
            ClassJavadoc classJavadoc = classJavadocCache.get(superType.getCanonicalName());
            if (classJavadoc == null) {
                classJavadoc = getSkinnyClassJavadoc(superType);
            }
            MethodJavadoc methodJavadoc = classJavadoc.findMatchingMethod(method);
            if (!methodJavadoc.fullyDescribes(method)) {
                MethodJavadoc recursiveMethodJavadoc = findOverriddenMethodJavadoc(method, superType, classJavadocCache);
                methodJavadoc = methodJavadoc.copyWithInheritance(recursiveMethodJavadoc);
            }

            if (!methodJavadoc.isEmpty()) {
                return methodJavadoc;
            }
        }

        return MethodJavadoc.createEmpty(method);
    }

    private static Method findBridgeMethod(Method method) {
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

    /**
     * Gets the Javadoc of the given constructor.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the method signature
     * with the correct documentation. If the client code's purpose is to loop through all methods doc, prefer using
     * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getMethods()} on the
     * returned class doc to retrieve method docs.
     *
     * @param method the constructor whose Javadoc you want to retrieve
     * @return the given constructor's Javadoc
     */
    public static MethodJavadoc getJavadoc(Constructor<?> method) {
        ClassJavadoc javadoc = getSkinnyClassJavadoc(method.getDeclaringClass());
        return javadoc.findMatchingConstructor(method);
    }

    /**
     * Gets the Javadoc of the given field.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the field name
     * with the correct documentation. If the client code's purpose is to loop through all fields doc, prefer using
     * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getFields()} on the
     * returned class doc to retrieve field docs.
     *
     * @param field the field whose Javadoc you want to retrieve
     * @return the given field's Javadoc
     */
    public static FieldJavadoc getJavadoc(Field field) {
        ClassJavadoc javadoc = getSkinnyClassJavadoc(field.getDeclaringClass());
        return javadoc.findMatchingField(field);
    }

    /**
     * Gets the Javadoc of the given enum constant.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the enum constant's
     * name with the correct documentation. If the client code's purpose is to loop through all enum constants docs,
     * prefer using {@link #getJavadoc(Class)} (or one of its overloads), and calling
     * {@link ClassJavadoc#getEnumConstants()} on the returned class doc to retrieve enum constant docs.
     *
     * @param enumValue the enum constant whose Javadoc you want to retrieve
     * @return the given enum constant's Javadoc
     */
    public static FieldJavadoc getJavadoc(Enum<?> enumValue) {
        ClassJavadoc javadoc = getSkinnyClassJavadoc(enumValue.getDeclaringClass());
        return javadoc.findMatchingEnumConstant(enumValue);
    }
}
