package com.github.therapi.runtimejavadoc;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ToTextListCommentVisitorTest {
    private ToTextListCommentVisitor visitor = new ToTextListCommentVisitor();

    @Test
    public void commentText() {
        visitor.commentText(
            "Excepteur sint occaecat cupidatat non proident,\n" +
                " sunt in culpa qui officia deserunt mollit\r\n" +
                " anim id est laborum."
        );

        List<String> expected = Arrays.asList(
            "Excepteur sint occaecat cupidatat non proident,",
                " sunt in culpa qui officia deserunt mollit",
                " anim id est laborum."
        );

        assertEquals(expected, visitor.build());
    }

    @Test
    public void link() {
        visitor.inlineLink(new Link(null, "java.lang.String", "toString", Collections.<String>emptyList() ));
        visitor.inlineLink(new Link(null, "java.lang.String", "toString", Collections.<String>emptyList() ));

        List<String> expected = Arrays.asList( "java.lang.String#toStringjava.lang.String#toString" );

        assertEquals(expected, visitor.build());
    }

    @Test
    public void inlineTag() {
        visitor.inlineTag("foo", "bar");

        List<String> expected = Arrays.asList( "bar" );

        assertEquals(expected, visitor.build());
    }

    @Test
    public void inlineValue() {
        visitor.inlineValue(new Value("java.lang.String","toString"));

        List<String> expected = Arrays.asList( "java.lang.String#toString" );

        assertEquals(expected, visitor.build());
    }

}
