package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.javadocResourceSuffix;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.github.therapi.runtimejavadoc.internal.JsonJavadocReader;

public class RuntimeJavadoc {

    private RuntimeJavadoc() {
        throw new AssertionError("not instantiable");
    }

    public static Optional<ClassJavadoc> getJavadoc(Class c) {
        return getJavadoc(c.getName(), c);
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName) {
        return getJavadoc(qualifiedClassName, RuntimeJavadoc.class);
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, ClassLoader classLoader) {
        final String resourceName = getResourceName(qualifiedClassName);
        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            return parseJavadocResource(qualifiedClassName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, Class loader) {
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

    private static Optional<ClassJavadoc> parseJavadocResource(String qualifiedClassName, InputStream is) throws IOException {
        if (is == null) {
            return Optional.empty();
        }

        try (InputStreamReader r = new InputStreamReader(is, UTF_8)) {
            JsonObject json = Json.parse(r).asObject();
            return JsonJavadocReader.readClassJavadoc(qualifiedClassName, json);
        }
    }

    public static Optional<MethodJavadoc> getJavadoc(Method method) {
        Optional<ClassJavadoc> javadoc = getJavadoc(method.getDeclaringClass());
        return javadoc.map(ClassJavadoc::getMethods).flatMap(mDocs -> findMethodJavadoc(mDocs, method));
    }

    private static Optional<MethodJavadoc> findMethodJavadoc(List<MethodJavadoc> methodDocs, Method method) {
        return methodDocs.stream().filter(m -> m.matches(method)).findAny();
    }

    public static Optional<FieldJavadoc> getJavadoc(Field field) {
        Optional<ClassJavadoc> javadoc = getJavadoc(field.getDeclaringClass());
        return javadoc.map(ClassJavadoc::getFields).flatMap(fDocs -> findFieldJavadoc(fDocs, field));
    }

    private static Optional<FieldJavadoc> findFieldJavadoc(List<FieldJavadoc> fieldDocs, Field field) {
        return fieldDocs.stream().filter(m -> m.getName().equals(field.getName())).findAny();
    }

    public static Optional<FieldJavadoc> getJavadoc(Enum<?> enumValue) {
        Optional<ClassJavadoc> javadoc = getJavadoc(enumValue.getDeclaringClass());
        return javadoc.map(ClassJavadoc::getEnumConstants).flatMap(fDocs -> findEnumValueJavadoc(fDocs, enumValue));
    }

    private static Optional<FieldJavadoc> findEnumValueJavadoc(List<FieldJavadoc> fieldDocs, Enum<?> enumValue) {
        return fieldDocs.stream().filter(m -> m.getName().equals(enumValue.name())).findAny();
    }
}
