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

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JavadocParser {

    private static final Pattern blockSeparator = Pattern.compile("^\\s*@(?=\\S)", Pattern.MULTILINE);

    private static final Pattern whitespace = Pattern.compile("\\s");

    private static ParamJavadoc parseParam(BlockTag t, String owningClass) {
        String[] paramNameAndComment = whitespace.split(t.value, 2);
        String paramName = paramNameAndComment[0];
        String paramComment = paramNameAndComment.length == 1 ? "" : paramNameAndComment[1];

        return new ParamJavadoc(paramName, CommentParser.parse(owningClass, paramComment));
    }

    public static ClassJavadoc parseClassJavadoc(String className, String javadoc, List<FieldJavadoc> fields,
                                                 List<FieldJavadoc> enumConstants, List<MethodJavadoc> methods, List<MethodJavadoc> constructors) {
        ParsedJavadoc parsed = parse(javadoc, className);
        return new ClassJavadoc(className, parsed.getDescription(), fields, enumConstants, methods,
                constructors, parsed.getOtherDocs(), parsed.getSeeAlsoDocs(), parsed.getParamDocs());
    }

    public static FieldJavadoc parseFieldJavadoc(String owningClass, String fieldName, String javadoc) {
        ParsedJavadoc parsed = parse(javadoc, owningClass);
        return new FieldJavadoc(fieldName, parsed.getDescription(), parsed.getOtherDocs(), parsed.getSeeAlsoDocs());
    }

    public static MethodJavadoc parseMethodJavadoc(String owningClass, String methodName, List<String> paramTypes, String javadoc) {
        ParsedJavadoc parsed = parse(javadoc, owningClass);
        return new MethodJavadoc(methodName, paramTypes, parsed.getDescription(), parsed.getParamDocs(),
                parsed.getThrowsDocs(), parsed.getOtherDocs(), parsed.getReturns(), parsed.getSeeAlsoDocs());
    }

    private static ParsedJavadoc parse(String javadoc, String owningClass) {
        String[] blocks = blockSeparator.split(javadoc);

        List<ParamJavadoc> paramDocs = new ArrayList<>();
        List<SeeAlsoJavadoc> seeAlsoDocs = new ArrayList<>();
        List<ThrowsJavadoc> throwsDocs = new ArrayList<>();
        List<OtherJavadoc> otherDocs = new ArrayList<>();
        Comment returns = null;

        for (int i = 1; i < blocks.length; i++) {
            BlockTag blockTag = parseBlockTag(blocks[i]);
            switch (blockTag.name) {
                case "param":
                    paramDocs.add(parseParam(blockTag, owningClass));
                    break;
                case "return":
                    returns = CommentParser.parse(owningClass, blockTag.value);
                    break;
                case "see":
                    SeeAlsoJavadoc seeAlso = SeeAlsoParser.parseSeeAlso(owningClass, blockTag.value);
                    if (seeAlso != null) {
                        seeAlsoDocs.add(seeAlso);
                    }
                    break;
                case "throws":
                case "exception":
                    ThrowsJavadoc throwsDoc = ThrowsTagParser.parseTag(owningClass, blockTag.value);
                    if (throwsDoc != null) {
                        throwsDocs.add(throwsDoc);
                    }
                    break;
                default:
                    otherDocs.add(new OtherJavadoc(blockTag.name, CommentParser.parse(owningClass, blockTag.value)));
                    break;
            }
        }
        Comment description = CommentParser.parse(owningClass, blocks[0].trim());
        return new ParsedJavadoc(description, otherDocs, seeAlsoDocs, paramDocs, throwsDocs, returns);
    }

    private static BlockTag parseBlockTag(String block) {
        String[] s = whitespace.split(block.trim(), 2);
        String name = s[0];
        String value = s.length > 1 ? s[1] : "";
        return new BlockTag(name, value);
    }
}
