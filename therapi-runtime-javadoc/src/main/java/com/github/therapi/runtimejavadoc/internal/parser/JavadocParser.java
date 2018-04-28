package com.github.therapi.runtimejavadoc.internal.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;

public class JavadocParser {

    private static final Pattern blockSeparator = Pattern.compile("^\\s*@(?=\\S)", Pattern.MULTILINE);

    private static final Pattern whitespace = Pattern.compile("\\s");

    private static Comment parseComment(String s) {
        List<CommentElement> commentElements = new ArrayList<>();
        commentElements.add(new CommentText(s));
        return new Comment(commentElements);
    }

    public static ClassJavadoc parseClassJavadoc(String className, String javadoc, List<FieldJavadoc> fields,
            List<FieldJavadoc> enumConstants, List<MethodJavadoc> methods) {
        ParsedJavadoc parsed = parse(javadoc);

        List<OtherJavadoc> otherDocs = new ArrayList<>();

        for (BlockTag t : parsed.getBlockTags()) {
            otherDocs.add(new OtherJavadoc(t.name, parseComment(t.value)));
        }

        return new ClassJavadoc(className, parseComment(parsed.getDescription()), fields, enumConstants, methods,
                otherDocs, new ArrayList<>());
    }

    public static FieldJavadoc parseFieldJavadoc(String fieldName, String javadoc) {
        ParsedJavadoc parsed = parse(javadoc);

        List<OtherJavadoc> otherDocs = new ArrayList<>();
        for (BlockTag t : parsed.getBlockTags()) {
            otherDocs.add(new OtherJavadoc(t.name, parseComment(t.value)));
        }

        return new FieldJavadoc(fieldName, parseComment(parsed.getDescription()), otherDocs, new ArrayList<>());
    }

    public static MethodJavadoc parseMethodJavadoc(String methodName, List<String> paramTypes, String javadoc) {
        ParsedJavadoc parsed = parse(javadoc);

        List<OtherJavadoc> otherDocs = new ArrayList<>();
        List<ParamJavadoc> paramDocs = new ArrayList<>();

        Comment returns = null;

        for (BlockTag t : parsed.getBlockTags()) {
            if (t.name.equals("param")) {
                String[] paramNameAndComment = whitespace.split(t.value, 2);
                String paramName = paramNameAndComment[0];
                String paramComment = paramNameAndComment.length == 1 ? "" :paramNameAndComment[1];

                paramDocs.add(new ParamJavadoc(paramName, parseComment(paramComment)));
            } else if (t.name.equals("return")) {
                returns = parseComment(t.value);
            } else {
                otherDocs.add(new OtherJavadoc(t.name, parseComment(t.value)));
            }
        }

        return new MethodJavadoc(methodName, paramTypes, parseComment(parsed.getDescription()), paramDocs,
                new ArrayList<>(), otherDocs, returns, new ArrayList<>());
    }

    private static ParsedJavadoc parse(String javadoc) {
        String[] blocks = blockSeparator.split(javadoc);

        ParsedJavadoc result = new ParsedJavadoc();
        result.description = blocks[0].trim();

        for (int i = 1; i < blocks.length; i++) {
            result.blockTags.add(parseBlockTag(blocks[i]));
        }
        return result;
    }

    private static BlockTag parseBlockTag(String block) {
        String[] s = whitespace.split(block.trim(), 2);
        String name = s[0];
        String value = s.length > 1 ? s[1] : "";
        return new BlockTag(name, value);
    }
}