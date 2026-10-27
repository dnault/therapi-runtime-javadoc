package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavadocParserTest {

    // https://github.com/dnault/therapi-runtime-javadoc/issues/73
    @Test
    public void givenReturnTagWithEmptyLink_expectEmptyReturnComment() {
        // Raw doc comment as stored by the annotation processor for the example in the issue
        String javadoc = " list\n\n @param request\n @return {@link }\n";

        MethodJavadoc doc = JavadocParser.parseMethodJavadoc(
            "com.example.Example", "list", Collections.singletonList("java.lang.String"), javadoc);

        assertEquals(Collections.singletonList(new CommentText("list")), doc.getComment().getElements());
        assertEquals(Collections.emptyList(), doc.getReturns().getElements());
    }
}
