package com.github.therapi.runtimejavadoc;

import java.io.IOException;

public class Example {
    private static final RuntimeJavadocReader reader = new RuntimeJavadocReader();
    private static final CommentFormatter formatter = new CommentFormatter();

    public static void printJavadoc(String fullyQualifiedClassName) throws IOException {
        ClassJavadoc classDoc = reader.getDocumentation(fullyQualifiedClassName);

        System.out.println(classDoc.getName() + ": " + format(classDoc.getComment()));
        // @see tags
        for (SeeAlsoJavadoc see : classDoc.getSeeAlso()) {
            System.out.println("See also: " + see.getLink());
        }
        // miscellaneous and custom javadoc tags (@author, etc.)
        for (OtherJavadoc other : classDoc.getOther()) {
            System.out.println(other.getName() + ": " + format(other.getComment()));
        }

        System.out.println("METHODS");
        for (MethodJavadoc methodDoc : classDoc.getMethods()) {
            System.out.println(methodDoc.getName() + ": " + format(methodDoc.getComment());
            System.out.println("signature " + methodDoc.getSignature());
            System.out.println("returns " + format(methodDoc.getReturns()));

            for (SeeAlsoJavadoc see : methodDoc.getSeeAlso()) {
                System.out.println("See also: " + see.getLink());
            }
            for (OtherJavadoc other : methodDoc.getOther()) {
                System.out.println(other.getName() + ": " + format(other.getComment()));
            }
            for (ParamJavadoc paramDoc : methodDoc.getParams()) {
                System.out.println("param " + paramDoc.getName() + " " + format(paramDoc.getComment()));
            }
            for (ThrowsJavadoc throwsDoc : methodDoc.getThrows()) {
                System.out.println("throws " + throwsDoc.getName() + " " + format(throwsDoc.getComment()));
            }
        }
    }

    private static String format(Comment c) {
        return formatter.format(c);
    }
}
