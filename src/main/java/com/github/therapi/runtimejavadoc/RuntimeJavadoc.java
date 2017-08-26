package com.github.therapi.runtimejavadoc;

import static com.github.therapi.runtimejavadoc.internal.JavadocAnnotationProcessor.javadocClassNameSuffix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class RuntimeJavadoc {
    private RuntimeJavadoc() {
    }

    public static Optional<ClassJavadoc> getJavadoc(Class c) {
        return getJavadoc(c.getName(), c.getClassLoader());
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName) {
        return getJavadoc(qualifiedClassName, RuntimeJavadoc.class.getClassLoader());
    }

    public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, ClassLoader classLoader) {
        try {
            Class<?> javadocClass = Class.forName(qualifiedClassName + javadocClassNameSuffix(), true, classLoader);
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
