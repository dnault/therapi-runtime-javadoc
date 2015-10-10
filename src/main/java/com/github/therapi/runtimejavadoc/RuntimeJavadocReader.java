package com.github.therapi.runtimejavadoc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class RuntimeJavadocReader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClassJavadoc getDocumentation(String qualifiedClassName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/" + qualifiedClassName.replace(".", "/").replace("$", "/") + ".javadoc.json")) {
            return is == null ? null : objectMapper.readValue(is, ClassJavadoc.class);
        }
    }
}
