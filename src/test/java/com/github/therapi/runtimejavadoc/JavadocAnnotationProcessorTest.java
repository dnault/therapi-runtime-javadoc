package com.github.therapi.runtimejavadoc;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertEquals;

import javax.tools.JavaFileObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.therapi.runtimejavadoc.internal.JavadocAnnotationProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

public class JavadocAnnotationProcessorTest {

    private static final String DOCUMENTED_CLASS = "javasource.foo.DocumentedClass";
    private static final String ANOTHER_DOCUMENTED_CLASS = "javasource.bar.AnotherDocumentedClass";
    private static final String ANNOTATED_WITH_RETAIN_JAVADOC = "javasource.bar.YetAnotherDocumentedClass";

    private static List<JavaFileObject> sources() {
        List<JavaFileObject> files = new ArrayList<>();
        for (String resource : new String[]{
                "javasource/foo/DocumentedClass.java",
                "javasource/bar/AnotherDocumentedClass.java",
                "javasource/bar/YetAnotherDocumentedClass.java",
        }) {
            files.add(JavaFileObjects.forResource(resource));
        }
        return files;
    }

    // formatters are reusable and thread-safe
    private static final CommentFormatter formatter = new CommentFormatter();

    private static CompilationClassLoader compile(String options) {
        com.google.testing.compile.Compiler compiler = javac()
                .withProcessors(new JavadocAnnotationProcessor());

        if (options != null) {
            compiler = compiler.withOptions(options);
        }

        Compilation compilation = compiler.compile(sources());
        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningCount(0);

        return new CompilationClassLoader(compilation);
    }

    @Test
    public void classNameIsPreserved() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS);
            ClassJavadoc classJavadoc = expectJavadoc(c);
            assertEquals(DOCUMENTED_CLASS, classJavadoc.getName());
        }
    }

    @Test
    public void retainFromAllPackages() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }

        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void retainFromOtherPackage() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=some.other.package")) {
            expectNoJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectNoJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void retainFromPackageFoo() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=javasource.foo")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectNoJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void retainFromPackageFooAndBar() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=javasource.foo,javasource.bar")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void subpackagesAreRetained() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=javasource")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void methodsMatchDespiteOverload() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS);

            final String methodName = "frobulate";
            Method m1 = c.getDeclaredMethod(methodName, String.class, int.class);
            Method m2 = c.getDeclaredMethod(methodName, String.class, List.class);

            assertMethodMatches(m1, "Frobulate {@code a} by {@code b}");
            assertMethodMatches(m2, "Frobulate {@code a} by multiple oopsifizzle constants");
        }
    }

    private static void assertMethodMatches(Method method, String expectedDescription) {
        MethodJavadoc methodDoc = expectJavadoc(method);
        assertEquals(method.getName(), methodDoc.getName());

        String actualDesc = formatter.format(methodDoc.getComment());
        assertEquals(expectedDescription, actualDesc);
    }

    @Test
    public void nestedClassNameIsPreserved() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS + "$Nested");
            ClassJavadoc classJavadoc = expectJavadoc(c);
            assertEquals(DOCUMENTED_CLASS + ".Nested", classJavadoc.getName());
        }
    }

    private static ClassJavadoc expectJavadoc(Class<?> c) {
        return RuntimeJavadoc.getJavadoc(c)
                .orElseThrow(() -> new AssertionError("Missing Javadoc for " + c));
    }

    private static MethodJavadoc expectJavadoc(Method m) {
        return RuntimeJavadoc.getJavadoc(m)
                .orElseThrow(() -> new AssertionError("Missing Javadoc for " + m));
    }

    private static void expectNoJavadoc(Class<?> c) {
        assertEquals(Optional.empty(), RuntimeJavadoc.getJavadoc(c));
    }
}
