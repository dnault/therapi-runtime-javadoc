package com.github.therapi.runtimejavadoc;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.github.therapi.runtimejavadoc.internal.JsonJavadocReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.javadocResourceSuffix;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Allows access to Javadoc elements at runtime for code that was compiled using the
 * {@code therapi-runtime-javadoc-scribe} annotation processor.
 */
public class RuntimeJavadoc {

    private RuntimeJavadoc() {
        throw new AssertionError("not instantiable");
    }

    /**
     * Gets the Javadoc of the given class.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param clazz the class whose Javadoc you want to retrieve
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(Class clazz) {
        return getJavadoc(clazz.getName(), clazz);
    }

    /**
     * Gets the Javadoc of the given class.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param qualifiedClassName the fully qualified name of the class whose Javadoc you want to retrieve
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(String qualifiedClassName) {
        return getJavadoc(qualifiedClassName, RuntimeJavadoc.class);
    }

    /**
     * Gets the Javadoc of the given class, using the given {@link ClassLoader} to find the Javadoc resource.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param qualifiedClassName the fully qualified name of the class whose Javadoc you want to retrieve
     * @param classLoader        the class loader to use to find the Javadoc resource file
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(String qualifiedClassName, ClassLoader classLoader) {
        final String resourceName = getResourceName(qualifiedClassName);
        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            return parseJavadocResource(qualifiedClassName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the Javadoc of the given class, using the given {@link Class} object to load the Javadoc resource.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     *
     * @param qualifiedClassName the fully qualified name of the class whose Javadoc you want to retrieve
     * @param loader             the class object to use to find the Javadoc resource file
     * @return the Javadoc of the given class
     */
    public static ClassJavadoc getJavadoc(String qualifiedClassName, Class loader) {
        final String resourceName = getResourceName(qualifiedClassName);
        try (InputStream is = loader.getResourceAsStream("/" + resourceName)) {
            return parseJavadocResource(qualifiedClassName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getResourceName(String qualifiedClassName) {
        return qualifiedClassName.replace(".", "/") + javadocResourceSuffix();
    }

    private static ClassJavadoc parseJavadocResource(String qualifiedClassName, InputStream is) throws IOException {
        if (is == null) {
            return ClassJavadoc.createEmpty(qualifiedClassName);
        }

        try (InputStreamReader r = new InputStreamReader(is, UTF_8)) {
            JsonObject json = Json.parse(r).asObject();
            return JsonJavadocReader.readClassJavadoc(qualifiedClassName, json);
        }
    }

    /**
     * Gets the Javadoc of the given method.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the method signature
     * with the correct documentation. If the client code's purpose is to loop through all methods doc, prefer using
     * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getMethods()} on the
     * returned class doc to retrieve method docs.
     *
     * @param method the method whose Javadoc you want to retrieve
     * @return the given method's Javadoc
     */
    public static MethodJavadoc getJavadoc(Method method) {
        ClassJavadoc javadoc = getJavadoc(method.getDeclaringClass());
        return findMethodJavadoc(javadoc.getMethods(), method);
    }

    /**
     * Gets the Javadoc of the given constructor.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the method signature
     * with the correct documentation. If the client code's purpose is to loop through all methods doc, prefer using
     * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getMethods()} on the
     * returned class doc to retrieve method docs.
     *
     * @param method the constructor whose Javadoc you want to retrieve
     * @return the given constructor's Javadoc
     */
    public static MethodJavadoc getJavadoc(Constructor<?> method) {
        ClassJavadoc javadoc = getJavadoc(method.getDeclaringClass());
        return findMethodJavadoc(javadoc.getConstructors(), method);
    }

    private static MethodJavadoc findMethodJavadoc(List<MethodJavadoc> methodDocs, Method method) {
        for (MethodJavadoc methodJavadoc : methodDocs) {
            if (methodJavadoc.matches(method)) {
                return methodJavadoc;
            }
        }
        return MethodJavadoc.createEmpty(method);
    }

    private static MethodJavadoc findMethodJavadoc(List<MethodJavadoc> methodDocs, Constructor<?> method) {
        for (MethodJavadoc methodJavadoc : methodDocs) {
            if (methodJavadoc.matches(method)) {
                return methodJavadoc;
            }
        }
        return MethodJavadoc.createEmpty(method);
    }

    /**
     * Gets the Javadoc of the given field.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the field name
     * with the correct documentation. If the client code's purpose is to loop through all fields doc, prefer using
     * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getFields()} on the
     * returned class doc to retrieve field docs.
     *
     * @param field the field whose Javadoc you want to retrieve
     * @return the given field's Javadoc
     */
    public static FieldJavadoc getJavadoc(Field field) {
        ClassJavadoc javadoc = getJavadoc(field.getDeclaringClass());
        return findFieldJavadoc(javadoc.getFields(), field.getName());
    }

    /**
     * Gets the Javadoc of the given enum constant.
     * <p>
     * The return value is always non-null. If no Javadoc is available, the returned object's
     * {@link BaseJavadoc#isEmpty isEmpty()} method will return {@code true}.
     * <p>
     * Implementation note: this method first retrieves the Javadoc of the class, and then matches the enum constant's
     * name with the correct documentation. If the client code's purpose is to loop through all enum constants docs,
     * prefer using {@link #getJavadoc(Class)} (or one of its overloads), and calling
     * {@link ClassJavadoc#getEnumConstants()} on the returned class doc to retrieve enum constant docs.
     *
     * @param enumValue the enum constant whose Javadoc you want to retrieve
     * @return the given enum constant's Javadoc
     */
    public static FieldJavadoc getJavadoc(Enum<?> enumValue) {
        ClassJavadoc javadoc = getJavadoc(enumValue.getDeclaringClass());
        return findFieldJavadoc(javadoc.getEnumConstants(), enumValue.name());
    }

    private static FieldJavadoc findFieldJavadoc(List<FieldJavadoc> fieldDocs, String fieldName) {
        for (FieldJavadoc fDoc : fieldDocs) {
            if (fDoc.getName().equals(fieldName)) {
                return fDoc;
            }
        }
        return FieldJavadoc.createEmpty(fieldName);
    }
}
