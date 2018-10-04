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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.isBlank;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;

class CommentParser {

    private static final Pattern inlineTag = compile("\\{@(\\w+)(?:\\s+([\\w#][^}]+)?)?}");
    
    // https://regex101.com/r/3DoNfK/1
    private static final Pattern linkPattern = compile("^(?<classname>[\\w.]+)?(?:#(?<member>\\w+))?(?:\\((?<params>.*)\\))?(?:\\s(?<label>\\w+(?:\\s\\w+)*))?$");
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
        } else if ("value".equals(name)) {
            return createValueElement(owningClass, value);
        } else {
            return new InlineTag(name, value);
        }
    }
    
    private static InlineValue createValueElement(String owningClass, String value) {
        Matcher linkMatcher = valuePattern.matcher(value);
        if (!linkMatcher.matches()) {
            throw new AssertionError("Value didn't match regex format");
        }
        String classRef = linkMatcher.group("classname");
        String memberRef = linkMatcher.group("member");
        
        String effectiveClassName = classRef == null ? owningClass : classRef;
        return new InlineValue(new Value(effectiveClassName, memberRef));
    }

    private static InlineLink createLinkElement(String owningClass, String value) {
        Matcher linkMatcher = linkPattern.matcher(value);
        if (!linkMatcher.matches()) {
            throw new AssertionError("Link didn't match regex format");
        }
        String classRef = linkMatcher.group("classname");
        String memberRef = linkMatcher.group("member");
        String params = linkMatcher.group("params");
        String label = linkMatcher.group("label");
    
        String effectiveClassName = classRef == null ? owningClass : classRef;
        String effectiveLabel = label != null ? label : linkMatcher.group(0);
        return new InlineLink(new Link(effectiveLabel, effectiveClassName, memberRef, formatMember(params)));
    }
    
    private static List<String> formatMember(String params) {
		if (params != null && !params.trim().isEmpty()) {
			return asList(params.trim().split(",\\s*"));
		}
		return Collections.emptyList();
    }
}