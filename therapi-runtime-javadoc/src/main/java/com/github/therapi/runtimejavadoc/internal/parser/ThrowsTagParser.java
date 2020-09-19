package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.ClassResolver;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;

import java.util.regex.Pattern;


public class ThrowsTagParser {
    private static final Pattern whitespace = Pattern.compile("\\s");

    public static ThrowsJavadoc parseTag( String owningClass, ClassResolver classResolver, String value ) {
        if ( value == null || value.trim().length() == 0 ) {
            return null;
        }

        String[] exceptionClassAndComment = whitespace.split(value.trim(), 2);
        String   exceptionClass           = classResolver.resolveRef(exceptionClassAndComment[0]);
        String   comment                  = exceptionClassAndComment.length == 1 ? "" :exceptionClassAndComment[1];

        return new ThrowsJavadoc( exceptionClass, CommentParser.parse(owningClass,classResolver,comment) );
    }
}
