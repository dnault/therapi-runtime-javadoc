package com.github.therapi.runtimejavadoc;

import java.io.IOException;

public class Example {
    // formatters are reusable and thread-safe
    private static final CommentFormatter formatter = new CommentFormatter();

    public static void printJavadoc(String fullyQualifiedClassName) throws IOException {
        ClassJavadoc classDoc = RuntimeJavadoc.getJavadoc(fullyQualifiedClassName);
        if (classDoc.isEmpty()) { // optionally skip absent documentation
            System.out.println("no documentation for " + fullyQualifiedClassName);
            return;
        }

        System.out.println(classDoc.getName());
        System.out.println(format(classDoc.getComment()));
        System.out.println();

        // @see tags
        for (SeeAlsoJavadoc see : classDoc.getSeeAlso()) {
            System.out.println("See also: " + see.getLink());
        }
        // miscellaneous and custom javadoc tags (@author, etc.)
        for (OtherJavadoc other : classDoc.getOther()) {
            System.out.println(other.getName() + ": " + format(other.getComment()));
        }

        System.out.println();
        System.out.println("METHODS");

        for (MethodJavadoc methodDoc : classDoc.getMethods()) {
            System.out.println(methodDoc.getName() + methodDoc.getParamTypes());
            System.out.println(format(methodDoc.getComment()));
            System.out.println("  returns " + format(methodDoc.getReturns()));

            for (SeeAlsoJavadoc see : methodDoc.getSeeAlso()) {
                System.out.println("  See also: " + see.getLink());
            }
            for (OtherJavadoc other : methodDoc.getOther()) {
                System.out.println("  " + other.getName() + ": "
                        + format(other.getComment()));
            }
            for (ParamJavadoc paramDoc : methodDoc.getParams()) {
                System.out.println("  param " + paramDoc.getName() + " "
                        + format(paramDoc.getComment()));
            }
            for (ThrowsJavadoc throwsDoc : methodDoc.getThrows()) {
                System.out.println("  throws " + throwsDoc.getName() + " "
                        + format(throwsDoc.getComment()));
            }
            System.out.println();
        }
    }

    private static String format(Comment c) {
        return formatter.format(c);
    }
}
