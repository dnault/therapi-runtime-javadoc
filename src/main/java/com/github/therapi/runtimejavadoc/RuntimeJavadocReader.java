package com.github.therapi.runtimejavadoc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

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
        try (InputStream is = getClass().getResourceAsStream("/" + qualifiedClassName.replace(".", "/").replace("$", "/") + ".javadoc.json")) {
            return is == null ? null : objectMapper.readValue(is, ClassJavadoc.class);
        }
    }
}
