package com.github.therapi.runtimejavadoc;

import com.github.therapi.runtimejavadoc.scribe.JavadocAnnotationProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.lang.model.SourceVersion;
import java.util.List;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for record classes, which require Java 16 or later to compile.
 * Skipped when the tests run on an earlier JVM.
 */
public class RecordJavadocTest {

    private static final String DOCUMENTED_RECORD = "javasource.foo.DocumentedRecord";

    @BeforeAll
    public static void requireRecordSupport() {
        // compile-testing uses the compiler of the JVM running the tests.
        // Compare ordinals, because SourceVersion.RELEASE_16 doesn't exist in Java 8.
        assumeTrue(SourceVersion.latestSupported().ordinal() >= 16, "Records require Java 16 or later");
    }

    private static CompilationClassLoader compile(String options) {
        com.google.testing.compile.Compiler compiler = javac()
                .withProcessors(new JavadocAnnotationProcessor());

        if (options != null) {
            compiler = compiler.withOptions(options);
        }

        Compilation compilation = compiler.compile(JavaFileObjects.forResource("javasource/foo/DocumentedRecord.java"));
        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningCount(0);

        return new CompilationClassLoader(compilation);
    }

    @Test
    public void retainedUnlessPackageIsExcluded() throws Exception {
        for (String options : new String[]{
                null,
                "-Ajavadoc.packages=",
                "-Ajavadoc.packages=javasource.foo",
                "-Ajavadoc.packages=javasource.foo,javasource.bar",
                "-Ajavadoc.packages=javasource",
        }) {
            try (CompilationClassLoader classLoader = compile(options)) {
                expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            }
        }

        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=some.other.package")) {
            expectNoJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
        }
    }

    @Test
    public void nestedClassNameIsPreserved() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            ClassJavadoc classJavadoc = expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD + "$Nested"));
            assertEquals(DOCUMENTED_RECORD + ".Nested", classJavadoc.getName());

            expectNoJavadoc(classLoader.loadClass(DOCUMENTED_RECORD + "$NestedWithoutJavadoc"));
        }
    }

    @Test
    public void canGetRecordComponents() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_RECORD);
            ClassJavadoc classJavadoc = expectJavadoc(c);
            List<ParamJavadoc> components = classJavadoc.getRecordComponents();

            assertEquals("count", components.get(0).getName());
            assertEquals("lucky number", components.get(0).getComment().toString());

            assertEquals("color", components.get(1).getName());
            assertEquals("favorite color", components.get(1).getComment().toString());
        }
    }

    private static ClassJavadoc expectJavadoc(Class<?> c) {
        ClassJavadoc doc = RuntimeJavadoc.getJavadoc(c);
        if (doc == null || doc.isEmpty()) {
            throw new AssertionError("Missing Javadoc for " + c);
        }
        return doc;
    }

    private static void expectNoJavadoc(Class<?> c) {
        ClassJavadoc doc = RuntimeJavadoc.getJavadoc(c);
        assertNotNull(doc);
        assertTrue(doc.isEmpty());
        assertEquals(c.getName(), doc.getName());
    }
}
