package com.github.dnault.therapi.runtimejavadoc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class RuntimeJavadocReader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClassDocumentation getDocumentation(String qualifiedClassName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(qualifiedClassName + ".javadoc.json")) {
            return is == null ? null : objectMapper.readValue(is, ClassDocumentation.class);
        }
    }
}
