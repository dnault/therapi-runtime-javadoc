package com.therapi.javadoc.acctests;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.Link;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ExampleTest {
    @Test
    public void givenRelativeClassNameInSeeTag_expectClassNameToBeResolved() {
        ClassJavadoc         classJavadoc = RuntimeJavadoc.getJavadoc(Example.class);
        List<SeeAlsoJavadoc> expected     = Collections.singletonList( new SeeAlsoJavadoc( new Link( "Map", "java.util.Map", null, Collections.<String>emptyList() ) ) );
        List<SeeAlsoJavadoc> actual       = classJavadoc.getMethod( "method" ).getSeeAlso();

        assertEquals( expected, actual );
    }

    @Test
    public void givenRelativeExceptionInThrowsTag_expectExceptionClassNameToBeResolved() {
        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(Example.class);

        List<ThrowsJavadoc> expected = Collections.singletonList(
            new ThrowsJavadoc(
                "java.lang.RuntimeException",
                new Comment(
                    Collections.<CommentElement>singletonList(
                        new CommentText(
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt."
                        )
                    )
                )
            )
        );

        List<ThrowsJavadoc> actual = classJavadoc.getMethod( "method" ).getThrows();

        assertEquals( expected, actual );
    }
}
