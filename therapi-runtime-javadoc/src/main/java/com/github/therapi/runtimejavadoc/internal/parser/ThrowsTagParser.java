/*
 * Copyright 2015 David Nault and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.ThrowsJavadoc;

import java.util.regex.Pattern;


public class ThrowsTagParser {
    private static final Pattern whitespace = Pattern.compile("\\s");

    public static ThrowsJavadoc parseTag(String owningClass, String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        String[] exceptionClassAndComment = whitespace.split(value.trim(), 2);
        String exceptionClass = toFullClassName(owningClass, exceptionClassAndComment[0]);
        String comment = exceptionClassAndComment.length == 1 ? "" : exceptionClassAndComment[1];

        return new ThrowsJavadoc(exceptionClass, CommentParser.parse(owningClass, comment));
    }

    private static String toFullClassName(String owningClass, String exceptionClass) {
        return exceptionClass;
    }
}
