package com.github.therapi.runtimejavadoc.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;

public class JavadocParser {
    public static class ParsedJavadoc {
        String description;
        List<BlockTag> blockTags = new ArrayList<>();

        @Override
        public String toString() {
            return "ParsedJavadoc{" +
                    "description='" + description + '\'' +
                    ", blockTags=" + blockTags +
                    '}';
        }

        public String getDescription() {
            return description;
        }

        public List<BlockTag> getBlockTags() {
            return blockTags;
        }
    }

    public static class BlockTag {
        BlockTag(String nameAndValue) {
            String[] s = whitespace.split(nameAndValue, 2);
            name = s[0];
            value = s.length > 1 ? s[1] : "";
        }

        final String name;
        final String value;

        @Override
        public String toString() {
            return "@" + name + " : " + value;
        }
    }

    private static Comment parseComment(String s) {
        List<CommentElement> commentElements = new ArrayList<>();
        commentElements.add(new CommentText(s));
        return new Comment(commentElements);
    }

    public static ClassJavadoc parseClassJavadoc(String className, String javadoc, List<MethodJavadoc> methods) {
        ParsedJavadoc parsed = parse(javadoc);

        List<OtherJavadoc> otherDocs = new ArrayList<>();

        for (BlockTag t : parsed.getBlockTags()) {
            otherDocs.add(new OtherJavadoc(t.name, parseComment(t.value)));
        }

        return new ClassJavadoc(className, parseComment(parsed.getDescription()),
                otherDocs, new ArrayList<SeeAlsoJavadoc>(), methods);
    }

    public static MethodJavadoc parseMethodJavadoc(String methodName, String javadoc) {
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

        return new MethodJavadoc(methodName, "", parseComment(parsed.getDescription()),
                paramDocs,
                new ArrayList<ThrowsJavadoc>(),
                otherDocs,
                returns,
                new ArrayList<SeeAlsoJavadoc>());
    }

    private static final Pattern blockSeparator = Pattern.compile("^\\s*@(?=\\S)", Pattern.MULTILINE);
    private static final Pattern whitespace = Pattern.compile("\\s");

    public static ParsedJavadoc parse(String javadoc) {
        String[] blocks = blockSeparator.split(javadoc);

        ParsedJavadoc result = new ParsedJavadoc();
        result.description = blocks[0].trim();

        for (int i = 1; i < blocks.length; i++) {
            result.blockTags.add(new BlockTag(blocks[i].trim()));
        }
        return result;
    }
}
