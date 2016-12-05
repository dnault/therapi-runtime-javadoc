package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.replaceLast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reusable and thread-safe.
 */
public class RuntimeJavadocReader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns Javadoc for the specified class, or {@code null} if none is available
     *
     * @param qualifiedClassName name of the class
     * @return javadoc for the specified class, or {@code null} if none is available
     * @throws IOException if an error occurred while reading the javadoc resource
     */
    public ClassJavadoc getDocumentation(String qualifiedClassName) throws IOException {
        String resourcePath = "/" + qualifiedClassName.replace(".", "/").replace("$", ".") + ".javadoc.json";

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return objectMapper.readValue(is, ClassJavadoc.class);
            }
        }

        // The caller may have referred to an inner class by its canonical name (for example, "Outer.Inner").
        // If so, the first attempt converted this to "Outer/Inner" and found nothing; try again!
        resourcePath = replaceLast(resourcePath, "/", ".");

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            return is == null ? null : objectMapper.readValue(is, ClassJavadoc.class);
        }
    }

    public static Optional<ClassJavadoc> getJavadoc(Class c) {
        String packageName = c.getPackage().getName();
        String typeName = c.getCanonicalName();

        if (!packageName.isEmpty()) {
            typeName = typeName.substring(packageName.length() + 1);
            typeName = typeName.replace(".", "$");
            typeName = packageName + "." + typeName;
        }

        return getJavadoc(typeName);
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName) {

        try {
            Class javadocClass = Class.forName(qualifiedClassName + "Javadoc");
            Method javadocMethod = javadocClass.getMethod("getJavadoc");
            ClassJavadoc classJavadoc = (ClassJavadoc) javadocMethod.invoke(null);
            return Optional.ofNullable(classJavadoc);

        } catch (ClassNotFoundException e) {
            return Optional.empty();

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}