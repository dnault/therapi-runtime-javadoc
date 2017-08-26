package com.github.therapi.runtimejavadoc;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertEquals;

import java.net.URLClassLoader;

import com.github.therapi.runtimejavadoc.internal.JavadocAnnotationProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavadocAnnotationProcessorTest {
    private static final String TEST_CLASS_NAME = "javasource.DocumentedClass";
    private static final String TEST_CLASS_SRC_RESOURCE_NAME = TEST_CLASS_NAME.replace(".", "/") + ".java";

    private static URLClassLoader classLoader;

    @BeforeClass
    public static void setUp() throws Exception {
        Compilation compilation = javac()
                .withProcessors(new JavadocAnnotationProcessor())
                .compile(JavaFileObjects.forResource(TEST_CLASS_SRC_RESOURCE_NAME));
        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningCount(0);

        classLoader = new CompilationClassLoader(compilation);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        classLoader.close();
    }

    @Test
    public void classNameIsPreserved() throws Exception {
        Class c = classLoader.loadClass(TEST_CLASS_NAME);
        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(c).orElseThrow(
                () -> new RuntimeException("missing javadoc"));

        assertEquals(TEST_CLASS_NAME, classJavadoc.getName());
    }
}
