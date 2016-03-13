package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.replaceLast;

import java.io.IOException;
import java.io.InputStream;

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
}