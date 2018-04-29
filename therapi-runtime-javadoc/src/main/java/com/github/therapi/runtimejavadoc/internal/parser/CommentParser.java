package com.github.therapi.runtimejavadoc.internal.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.InlineTag;
import com.github.therapi.runtimejavadoc.Link;

class CommentParser {

    private static final Pattern inlineTag = Pattern.compile("\\{@(\\w+)\\s+(\\w[^}]+)\\}");

    private static final Pattern inlineTagSplitter = Pattern.compile("((?=(\\{@))|(?<=}))");

    private static final Pattern whitespace = Pattern.compile("\\s+");

    private static final Pattern linkRefSplitter = Pattern.compile("#");

    static Comment parse(String commentText) {
        String[] parts = inlineTagSplitter.split(commentText);

        List<CommentElement> elements = new ArrayList<>();
        for (String part : parts) {
            CommentElement elt = parseElement(part);

            if (elt instanceof CommentText) {
                CommentElement last = last(elements);
                if (last instanceof CommentText) {
                    CommentText merged = merge((CommentText) last, (CommentText) elt);
                    elements.set(elements.size() - 1, merged);
                    continue;
                }
            }

            elements.add(elt);
        }
        return new Comment(elements);
    }

    private static CommentElement parseElement(String elt) {
        Matcher matcher = inlineTag.matcher(elt);
        if (matcher.matches()) {
            return createTagElement(matcher.group(1), matcher.group(2));
        } else {
            return new CommentText(elt);
        }
    }

    private static CommentElement createTagElement(String name, String value) {
        if ("link".equals(name)) {
            return createLinkElement(value);
        }
        return new InlineTag(name, value);
    }

    private static InlineLink createLinkElement(String value) {
        String[] linkElts = whitespace.split(value, 2);
        String label = linkElts.length > 1 ? linkElts[1] : linkElts[0];

        String[] ref = linkRefSplitter.split(linkElts[0], 2);
        String classRef = ref[0];
        String memberRef = ref.length > 1 ? ref[1] : null;

        Link link = new Link(label, classRef, memberRef);
        return new InlineLink(link);
    }

    private static CommentElement last(List<CommentElement> elements) {
        if (elements.isEmpty()) {
            return null;
        }
        return elements.get(elements.size() - 1);
    }

    private static CommentText merge(CommentText left, CommentText right) {
        return new CommentText(left.getValue() + right.getValue());
    }
}
