package com.github.therapi.runtimejavadoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ToTextListCommentVisitor implements CommentVisitor {
    protected List<String> lines = new ArrayList<>();

    public void commentText(String value) {
        String[] splits = value.split("\r\n|\n");

        lines.addAll(Arrays.asList(splits));
    }

    public void inlineLink(Link link) {
        appendInlineText(link.toString());
    }

    public void inlineTag(String name, String value) {
        appendInlineText(value);
    }

    public void inlineValue(Value value) {
        appendInlineText(value.toString());
    }

    public List<String> build() {
        return lines;
    }

    private void appendInlineText(String txt) {
        if (txt == null) {
        } else if (lines.isEmpty()) {
            lines.add(txt);
        } else {
            int index = lines.size() - 1;
            String newLine = lines.get(index) + txt;

            lines.set(index, newLine);
        }
    }
}
