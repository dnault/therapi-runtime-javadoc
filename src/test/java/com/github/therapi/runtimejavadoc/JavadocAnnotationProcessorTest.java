package com.github.therapi.runtimejavadoc;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.time.LocalDate;
import java.util.List;

import com.github.therapi.runtimejavadoc.internal.JavadocAnnotationProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavadocAnnotationProcessorTest {

    private static final String TEST_CLASS_NAME = "javasource.DocumentedClass";
    private static final String TEST_METHOD_NAME = "frobulate";
    private static final String TEST_CLASS_SRC_RESOURCE_NAME = TEST_CLASS_NAME.replace(".", "/") + ".java";

    // formatters are reusable and thread-safe
    private static final CommentFormatter formatter = new CommentFormatter();

    private static URLClassLoader classLoader;

    @BeforeClass
    public static void setUp() {
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
        Class<?> c = classLoader.loadClass(TEST_CLASS_NAME);

        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(c).orElseThrow(
                () -> new RuntimeException("missing javadoc"));
        assertEquals(TEST_CLASS_NAME, classJavadoc.getName());
    }

    @Test
    public void methodsMatchDespiteOverload() throws Exception {
        Class<?> c = classLoader.loadClass(TEST_CLASS_NAME);

        Method m1 = c.getDeclaredMethod(TEST_METHOD_NAME, String.class, int.class);
        Method m2 = c.getDeclaredMethod(TEST_METHOD_NAME, String.class, List.class);

        assertMethodMatches(m1, "Frobulate {@code a} by {@code b}");
        assertMethodMatches(m2, "Frobulate {@code a} by multiple oopsifizzle constants");
    }

    private static void assertMethodMatches(Method method, String expectedDescription) {
        MethodJavadoc methodDoc = RuntimeJavadoc.getJavadoc(method).orElseThrow(
                () -> new RuntimeException("method javadoc not found"));
        assertEquals(method.getName(), methodDoc.getName());

        String actualDesc = formatter.format(methodDoc.getComment());
        assertEquals(expectedDescription, actualDesc);
    }
}
