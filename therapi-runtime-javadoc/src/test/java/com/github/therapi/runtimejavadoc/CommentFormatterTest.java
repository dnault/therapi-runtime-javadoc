package com.github.therapi.runtimejavadoc;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CommentFormatterTest {
    private final CommentFormatter formatter = new CommentFormatter();

    @Test
    public void formatterHandlesAllCommentElements() {
        Comment c = new Comment(Arrays.asList(
                new CommentText("before "),
                new InlineLink(new Link("label", "className", "memberName", null)),
                new CommentText(" "),
                new InlineTag("code", "List<String>"),
                new CommentText(" "),
                new InlineTag("literal", "<>&\""),
                new InlineTag("unrecognized", "foo"),
                new CommentText(" "),
                new CommentElement() {
                    @Override public String toString() {
                        return "unexpected element";
                    }
                },
                new CommentText(" after")
        ));

        String expected = "before {@link className#memberName label} <code>List&lt;String&gt;</code> &lt;&gt;&amp;&quot;{@unrecognized foo} unexpected element after";
        assertEquals(expected, formatter.format(c));
    }
}