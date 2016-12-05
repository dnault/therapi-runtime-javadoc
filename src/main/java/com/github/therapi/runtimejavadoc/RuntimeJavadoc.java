package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.JavadocAnnotationProcessor.javadocMethodNameSuffix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class RuntimeJavadoc {
    private RuntimeJavadoc() {
    }

    public static Optional<ClassJavadoc> getJavadoc(Class c) {
        return getJavadoc(c.getName());
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName) {
        try {
            Class<?> javadocClass = Class.forName(qualifiedClassName + javadocMethodNameSuffix());
            Method javadocMethod = javadocClass.getMethod("getJavadoc");
            ClassJavadoc classJavadoc = (ClassJavadoc) javadocMethod.invoke(null);
            return Optional.ofNullable(classJavadoc);

        } catch (ClassNotFoundException e) {
            return Optional.empty();

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
