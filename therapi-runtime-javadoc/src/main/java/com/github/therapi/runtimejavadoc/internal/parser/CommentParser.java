package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.InlineTag;
import com.github.therapi.runtimejavadoc.InlineValue;
import com.github.therapi.runtimejavadoc.Link;
import com.github.therapi.runtimejavadoc.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.isBlank;
import static java.util.regex.Pattern.compile;

class CommentParser {

    private static final Pattern inlineTag = compile("\\{@([^\\s}]+)[ \\t]*([^}]+)?}");

    // https://regex101.com/r/KhEo62/4
    private static final Pattern valuePattern = compile("^(?:(?<classname>[\\w.]+)#)?#?(?<member>\\w+)$");

    static Comment parse(String owningClass, String commentText) {
        return isBlank(commentText) ? Comment.createEmpty() : new Comment(parseElements(owningClass, commentText.trim()));
    }

    private static List<CommentElement> parseElements(String owningClass, String commentText) {
        Matcher matcher = inlineTag.matcher(commentText);
        List<CommentElement> elements = new ArrayList<>();
        int pos = 0;
        while (matcher.find()) {
            int start = matcher.start();
            if (start > pos) {
                elements.add(new CommentText(commentText.substring(pos, start)));
            }
            CommentElement elt = createTagElement(owningClass, matcher.group(1), matcher.group(2));
            if (elt != null) {
                elements.add(elt);
            }
            pos = matcher.end();
        }

        if (pos < commentText.length()) {
            elements.add(new CommentText(commentText.substring(pos)));
        }
        return elements;
    }

    /**
     * @return null if tag is malformed
     */
    private static CommentElement createTagElement(String owningClass, String name, String value) {
        if ("link".equals(name)) {
            return createLinkElement(owningClass, value);
        } else if ("value".equals(name)) {
            return createValueElement(owningClass, value);
        } else {
            return new InlineTag(name, value);
        }
    }

    /**
     * @return null if tag is malformed
     */
    private static InlineValue createValueElement(String owningClass, String value) {
        if (value == null || value.trim().isEmpty()) {
            return new InlineValue(new Value(null, null));
        }

        Matcher linkMatcher = valuePattern.matcher(value);
        if (!linkMatcher.matches()) {
            // malformed reference
            return null;
        }
        String classRef = linkMatcher.group("classname");
        String memberRef = linkMatcher.group("member");

        String effectiveClassName = classRef == null ? owningClass : classRef;
        return new InlineValue(new Value(effectiveClassName, memberRef));
    }

    private static InlineLink createLinkElement(String owningClass, String value) {
        Link javadocLink = LinkParser.createLinkElement(owningClass, value);
        if (javadocLink == null) {
            // malformed link
            return null;
        }
        return new InlineLink(javadocLink);
    }
}