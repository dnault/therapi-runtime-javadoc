package com.github.therapi.runtimejavadoc;

import com.github.therapi.runtimejavadoc.scribe.JavadocAnnotationProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JavadocAnnotationProcessorTest {

    private static final String DOCUMENTED_RECORD = "javasource.foo.DocumentedRecord";
    private static final String DOCUMENTED_CLASS = "javasource.foo.DocumentedClass";
    private static final String DOCUMENTED_ENUM = "javasource.foo.DocumentedEnum";
    private static final String COMPLEX_ENUM = "javasource.foo.ComplexEnum";
    private static final String ANOTHER_DOCUMENTED_CLASS = "javasource.bar.AnotherDocumentedClass";
    private static final String ANNOTATED_WITH_RETAIN_JAVADOC = "javasource.bar.YetAnotherDocumentedClass";
    private static final String UNDOCUMENTED = "javasource.bar.UndocumentedClass";
    private static final String BLANK_COMMENTS = "javasource.bar.BlankDocumentation";
    private static final String METHOD_DOC_BUT_NO_CLASS_DOC = "javasource.bar.OnlyMethodDocumented";
    private static final String DEFAULT_PACKAGE_CLASS = "DefaultPackageResident";


    private static List<JavaFileObject> sources() {
        List<JavaFileObject> files = new ArrayList<>();
        for (String resource : new String[]{
                "javasource/foo/DocumentedRecord.java",
                "javasource/foo/DocumentedClass.java",
                "javasource/foo/DocumentedEnum.java",
                "javasource/foo/ComplexEnum.java",
                "javasource/bar/AnotherDocumentedClass.java",
                "javasource/bar/YetAnotherDocumentedClass.java",
                "javasource/bar/UndocumentedClass.java",
                "javasource/bar/BlankDocumentation.java",
                "javasource/bar/OnlyMethodDocumented.java",
                "DefaultPackageResident.java",
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
            expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }

        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void retainFromOtherPackage() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=some.other.package")) {
            expectNoJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectNoJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectNoJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void retainFromPackageFoo() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=javasource.foo")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectNoJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void retainFromPackageFooAndBar() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=javasource.foo,javasource.bar")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void subpackagesAreRetained() throws Exception {
        try (CompilationClassLoader classLoader = compile("-Ajavadoc.packages=javasource")) {
            expectJavadoc(classLoader.loadClass(DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(ANOTHER_DOCUMENTED_CLASS));
            expectJavadoc(classLoader.loadClass(DOCUMENTED_RECORD));
            expectJavadoc(classLoader.loadClass(ANNOTATED_WITH_RETAIN_JAVADOC));
        }
    }

    @Test
    public void fieldsMatch() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS);

            Field f = c.getDeclaredField("myField");
            assertFieldDocMatches(f, "I'm a useful field, maybe.");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void enumsAreRetrieved() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<? extends Enum> c = (Class<? extends Enum<?>>) classLoader.loadClass(DOCUMENTED_ENUM);

            assertEnumConstantMatches(Enum.valueOf(c, "FOO11"), "This is the FOO11 value documentation");
            assertEnumConstantMatches(Enum.valueOf(c, "BAR22"), "This is the BAR22 value documentation");
            assertEnumConstantMatches(Enum.valueOf(c, "BAZ33"), "This is the BAZ33 value documentation");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void complexEnumsAreRetrieved() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<? extends Enum> c = (Class<? extends Enum<?>>) classLoader.loadClass(COMPLEX_ENUM);

            assertEnumConstantMatches(Enum.valueOf(c, "FOO11"), "This is the FOO11 value documentation");
            assertEnumConstantMatches(Enum.valueOf(c, "BAR22"), "This is the BAR22 value documentation");
            assertEnumConstantMatches(Enum.valueOf(c, "BAZ33"), "This is the BAZ33 value documentation");

            Field f = c.getDeclaredField("content");
            assertFieldDocMatches(f, "Content field description.");
        }
    }

    private static void assertFieldDocMatches(Field field, String expectedDoc) {
        FieldJavadoc fieldJavadoc = RuntimeJavadoc.getJavadoc(field);
        assertNotNull(fieldJavadoc);
        assertEquals(field.getName(), fieldJavadoc.getName());
        assertEquals(expectedDoc, fieldJavadoc.getComment().toString());
    }

    private static void assertEnumConstantMatches(Enum enumConstant, String expectedDoc) {
        FieldJavadoc enumConstantDoc = expectJavadoc(enumConstant);
        assertEquals(enumConstant.name(), enumConstantDoc.getName());
        String actualDesc = formatter.format(enumConstantDoc.getComment());
        assertEquals(expectedDoc, actualDesc);
    }

    @Test
    public void canReadConstructorParameter() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS);

            Constructor<?> m2 = c.getDeclaredConstructor(String.class);
            MethodJavadoc ctorJavadoc = RuntimeJavadoc.getJavadoc(m2);
            List<ParamJavadoc> params = ctorJavadoc.getParams();
            assertEquals(1, params.size());
            ParamJavadoc paramDoc = params.get(0);
            assertEquals("ignore", paramDoc.getName());
            assertEquals("I'm a parameter!", formatter.format(paramDoc.getComment()));
        }
    }

    @Test
    public void constructorsMatchDespiteOverload() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS);

            Constructor<?> m1 = c.getDeclaredConstructor();
            Constructor<?> m2 = c.getDeclaredConstructor(String.class);

            assertConstructorMatches(m1, "I'm a constructor!");
            assertConstructorMatches(m2, "I'm another constructor!");

            Constructor<?> undocumented = c.getDeclaredConstructor(Integer.class);
            assertTrue(RuntimeJavadoc.getJavadoc(undocumented).isEmpty());
            assertEquals("<init>", RuntimeJavadoc.getJavadoc(undocumented).getName());
        }
    }

    @Test
    public void methodsMatchDespiteOverload() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS);

            final String methodName = "frobulate";
            Method m1 = c.getDeclaredMethod(methodName, String.class, int.class);
            Method m2 = c.getDeclaredMethod(methodName, String.class, List.class);

            assertMethodMatches(m1, "Frobulate <code>a</code> by <code>b</code>");
            assertMethodMatches(m2, "Frobulate <code>a</code> by multiple oopsifizzle constants");
        }
    }

    private static void assertConstructorMatches(Constructor<?> method, String expectedDescription) {
        MethodJavadoc methodDoc = expectJavadoc(method);
        assertEquals("<init>", methodDoc.getName());

        String actualDesc = formatter.format(methodDoc.getComment());
        assertEquals(expectedDescription, actualDesc);
    }

    private static void assertMethodMatches(Method method, String expectedDescription) {
        MethodJavadoc methodDoc = expectJavadoc(method);
        assertEquals(method.getName(), methodDoc.getName());

        String actualDesc = formatter.format(methodDoc.getComment());
        assertEquals(expectedDescription, actualDesc);

        assertEquals(methodDoc.getSeeAlso().size(), 4);

        SeeAlsoJavadoc seeAlso1 = methodDoc.getSeeAlso().get(0);
        assertEquals(seeAlso1.getSeeAlsoType(), SeeAlsoJavadoc.SeeAlsoType.JAVADOC_LINK);
        assertEquals(seeAlso1.getLink().getReferencedClassName(), "com.github.therapi.runtimejavadoc.DocumentedClass");
        assertNull(seeAlso1.getLink().getReferencedMemberName());
        assertEquals(seeAlso1.getLink().getParams().size(), 0);
        assertEquals(seeAlso1.getLink().getLabel(), "Hey, that's this class!");

        SeeAlsoJavadoc seeAlso2 = methodDoc.getSeeAlso().get(1);
        assertEquals(seeAlso2.getSeeAlsoType(), SeeAlsoJavadoc.SeeAlsoType.JAVADOC_LINK);
        assertEquals(seeAlso2.getLink().getReferencedClassName(), "javasource.foo.DocumentedClass");
        assertEquals(seeAlso2.getLink().getReferencedMemberName(), "someOtherMethod");
        assertEquals(seeAlso2.getLink().getParams().size(), 0);
        assertEquals(seeAlso2.getLink().getLabel(), "#someOtherMethod()");

        SeeAlsoJavadoc seeAlso3 = methodDoc.getSeeAlso().get(2);
        assertEquals(seeAlso3.getSeeAlsoType(), SeeAlsoJavadoc.SeeAlsoType.STRING_LITERAL);
        assertEquals(seeAlso3.getStringLiteral(), "Moomoo boy went straight to Moomoo land. Land of the moomoo's");

        SeeAlsoJavadoc seeAlso4 = methodDoc.getSeeAlso().get(3);
        assertEquals(seeAlso4.getSeeAlsoType(), SeeAlsoJavadoc.SeeAlsoType.HTML_LINK);
        assertEquals(seeAlso4.getHtmlLink().getLink(), "http://www.moomoo.land");
        assertEquals(seeAlso4.getHtmlLink().getText(), "Moomoo land");
    }

    @Test
    public void nestedClassNameIsPreserved() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            {
                Class<?> c = classLoader.loadClass(DOCUMENTED_CLASS + "$Nested");
                ClassJavadoc classJavadoc = expectJavadoc(c);
                assertEquals(DOCUMENTED_CLASS + ".Nested", classJavadoc.getName());
            }

            {
                Class<?> c = classLoader.loadClass(DOCUMENTED_RECORD + "$Nested");
                ClassJavadoc classJavadoc = expectJavadoc(c);
                assertEquals(DOCUMENTED_RECORD + ".Nested", classJavadoc.getName());
            }
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

    @Test
    public void nestedInDefaultPackage() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DEFAULT_PACKAGE_CLASS + "$Nested");
            ClassJavadoc classJavadoc = expectJavadoc(c);
            assertEquals(DEFAULT_PACKAGE_CLASS + ".Nested", classJavadoc.getName());
        }
    }

    @Test
    public void defaultPackage() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> c = classLoader.loadClass(DEFAULT_PACKAGE_CLASS);
            ClassJavadoc classJavadoc = expectJavadoc(c);
            assertEquals(DEFAULT_PACKAGE_CLASS, classJavadoc.getName());
        }
    }

    @Test
    public void noCompanionGeneratedForUndocumentedClass() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            expectNoJavadoc(classLoader.loadClass(UNDOCUMENTED));
            expectNoJavadoc(classLoader.loadClass(BLANK_COMMENTS));
        }
    }

    @Test
    public void companionGeneratedForClassWithMethodDocButNoClassDoc() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            expectJavadoc(classLoader.loadClass(METHOD_DOC_BUT_NO_CLASS_DOC));
        }
    }

    @Test
    public void malformedLinksAreOmitted() throws Exception {
        try (CompilationClassLoader classLoader = compile(null)) {
            Class<?> clazz = classLoader.loadClass(DOCUMENTED_CLASS);
            Method method = clazz.getDeclaredMethod("malformedLinks");
            MethodJavadoc doc = RuntimeJavadoc.getJavadoc(method);
            assertEquals(emptyList(), doc.getSeeAlso());
            assertEquals("Foo ", formatter.format(doc.getComment()));
        }
    }

    private static ClassJavadoc expectJavadoc(Class<?> c) {
        return assertPresent(RuntimeJavadoc.getJavadoc(c), "Missing Javadoc for " + c);
    }

    private static MethodJavadoc expectJavadoc(Method m) {
        return assertPresent(RuntimeJavadoc.getJavadoc(m), "Missing Javadoc for " + m);
    }

    private static MethodJavadoc expectJavadoc(Constructor<?> m) {
        return assertPresent(RuntimeJavadoc.getJavadoc(m), "Missing Javadoc for " + m);
    }

    private static FieldJavadoc expectJavadoc(Enum<?> e) {
        return assertPresent(RuntimeJavadoc.getJavadoc(e), "Missing Javadoc for " + e);
    }

    private static void expectNoJavadoc(Class<?> c) {
        ClassJavadoc doc = RuntimeJavadoc.getJavadoc(c);
        assertNotNull(doc);
        assertTrue(doc.isEmpty());
        assertEquals(c.getName(), doc.getName());
    }

    private static <T extends BaseJavadoc> T assertPresent(T value, String msg) {
        if (value == null || value.isEmpty()) {
            throw new AssertionError(msg);
        }
        return value;
    }
}
