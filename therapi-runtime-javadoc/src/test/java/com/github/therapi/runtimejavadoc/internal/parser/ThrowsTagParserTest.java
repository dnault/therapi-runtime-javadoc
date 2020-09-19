package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class ThrowsTagParserTest {
    private ClassResolverMock classResolverMock = new ClassResolverMock();

    @Test
    public void givenNullText_expectNoDocs() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, null );

        assertNull( doc );
    }
    @Test
    public void givenNoExceptionOrDescription_expectNoDocs() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, "" );

        assertNull( doc );
    }

    @Test
    public void givenFullyQualifiedExceptionAndNoDescription_expectEmptyDescription() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, "com.github.voyage.OutOfFuelException" );

        assertEquals( "com.github.voyage.OutOfFuelException", doc.getName() );
        assertEquals( Collections.emptyList(), doc.getComment().getElements() );
    }

    @Test
    public void givenWhitespaceAroundFullyQualifiedExceptionAndNoDescription_expectEmptyDescription() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, "   com.github.voyage.OutOfFuelException   " );

        assertEquals( "com.github.voyage.OutOfFuelException", doc.getName() );
        assertEquals( Collections.emptyList(), doc.getComment().getElements() );
    }

    @Test
    public void givenRelativeExceptionAndNoDescription_expectExceptionToNotBeResolvedYet() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, "OutOfFuelException" );

        assertEquals( "OutOfFuelException", doc.getName() );
        assertEquals( Collections.emptyList(), doc.getComment().getElements() );
    }

    @Test
    public void givenRelativeExceptionAndNoThatIsResolvable_expectFullyQualifiedException() {
        classResolverMock.addMapping( "OutOfFuelException", "com.hyperdyne.systems.OutOfFuelException" );
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, "OutOfFuelException" );

        assertEquals( "com.hyperdyne.systems.OutOfFuelException", doc.getName() );
        assertEquals( Collections.emptyList(), doc.getComment().getElements() );
    }

    @Test
    public void givenFullyQualifiedExceptionWithASingleLineDescription_expectDescription() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag( "com.github.Mission", classResolverMock, "com.github.voyage.OutOfFuelException Lorem ipsum dolor sit amet" );

        assertEquals( "com.github.voyage.OutOfFuelException", doc.getName() );
        assertEquals( Arrays.asList(new CommentText("Lorem ipsum dolor sit amet")), doc.getComment().getElements() );
    }

    @Test
    public void givenFullyQualifiedExceptionWithMultiLineDescription_expectDescription() {
        ThrowsJavadoc doc = ThrowsTagParser.parseTag(
            "com.github.Mission",
            classResolverMock,
            "  com.github.voyage.OutOfFuelException   Lorem ipsum dolor sit amet, consectetur \n" +
                  "                                     adipiscing elit, sed do eiusmod tempor incididunt.  " );

        assertEquals( "com.github.voyage.OutOfFuelException", doc.getName() );
        assertEquals( Arrays.asList(new CommentText("Lorem ipsum dolor sit amet, consectetur \n" +
            "                                     adipiscing elit, sed do eiusmod tempor incididunt.")), doc.getComment().getElements() );
    }
}
