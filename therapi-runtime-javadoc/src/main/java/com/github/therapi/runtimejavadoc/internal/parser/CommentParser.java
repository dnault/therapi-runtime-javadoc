package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.InlineTag;
import com.github.therapi.runtimejavadoc.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.isBlank;

class CommentParser {

    private static final Pattern inlineTag = Pattern.compile("\\{@(\\w+)(?:\\s+([\\w#][^}]+)?)?}");

    private static final Pattern whitespace = Pattern.compile("\\s+");

    private static final Pattern linkRefSplitter = Pattern.compile("#");

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
            elements.add(elt);
            pos = matcher.end();
        }

        if (pos < commentText.length()) {
            elements.add(new CommentText(commentText.substring(pos)));
        }
        return elements;
    }

    private static CommentElement createTagElement(String owningClass, String name, String value) {
        if ("link".equals(name)) {
            return createLinkElement(owningClass, value);
        }
        return new InlineTag(name, value);
    }

    private static InlineLink createLinkElement(String owningClass, String value) {
        String[] linkElts = whitespace.split(value, 2);
        String label = linkElts.length > 1 ? linkElts[1] : linkElts[0];

        String[] ref = linkRefSplitter.split(linkElts[0], 2);
        String classRef = ref[0];
        String memberRef = ref.length > 1 ? ref[1] : null;

        Link link = new Link(label, classRef.isEmpty() ? owningClass : classRef, memberRef);
        return new InlineLink(link);
    }
}
