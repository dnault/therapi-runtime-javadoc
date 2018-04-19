package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.classDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.javadocResourceSuffix;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.github.therapi.runtimejavadoc.internal.JavadocParser;

public class RuntimeJavadoc {

    private RuntimeJavadoc() {
        throw new AssertionError("not instantiable");
    }

    public static Optional<ClassJavadoc> getJavadoc(Class c) {
        return getJavadoc(c.getName(), c);
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName) {
        return getJavadoc(qualifiedClassName, RuntimeJavadoc.class);
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, ClassLoader classLoader) {
        final String resourceName = getResourceName(qualifiedClassName);
        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            return parseJavadocResource(qualifiedClassName, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, Class loader) {
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

    private static Optional<ClassJavadoc> parseJavadocResource(String qualifiedClassName, InputStream is) throws IOException {
        if (is == null) {
            return Optional.empty();
        }

        try (InputStreamReader r = new InputStreamReader(is, UTF_8)) {
            JsonObject json = Json.parse(r).asObject();

            JsonArray methodArray = json.get(methodsFieldName()).asArray();
            List<MethodJavadoc> methods = new ArrayList<>(methodArray.size());
            for (JsonValue methodValue : methodArray) {
                JsonObject method = methodValue.asObject();
                String methodName = method.getString(methodNameFieldName(), null);

                JsonArray paramTypesArray = method.get(paramTypesFieldName()).asArray();
                List<String> paramTypes = new ArrayList<>(paramTypesArray.size());
                for (JsonValue v : paramTypesArray) {
                    paramTypes.add(v.asString());
                }

                String methodDoc = method.getString(methodDocFieldName(), null);

                methods.add(JavadocParser.parseMethodJavadoc(methodName, paramTypes, methodDoc));
            }
            String className = qualifiedClassName.replace("$", ".");
            return Optional.of(JavadocParser.parseClassJavadoc(className, json.getString(classDocFieldName(), null), methods));
        }
    }

    public static Optional<MethodJavadoc> getJavadoc(Method method) {
        Optional<ClassJavadoc> javadoc = getJavadoc(method.getDeclaringClass());
        return javadoc.map(ClassJavadoc::getMethods).flatMap(mDocs -> findMethodJavadoc(mDocs, method));
    }

    private static Optional<MethodJavadoc> findMethodJavadoc(List<MethodJavadoc> methodDocs, Method method) {
        return methodDocs.stream().filter(m -> m.matches(method)).findAny();
    }
}
