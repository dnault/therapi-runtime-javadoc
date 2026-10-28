package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.CommentFormatter;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc.SeeAlsoType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    // https://github.com/dnault/therapi-runtime-javadoc/issues/79
    @Test
    public void givenEmptyCodeTag_expectEmptyCodeElement() {
        MethodJavadoc doc = JavadocParser.parseMethodJavadoc(
            "com.example.Example", "list", Collections.emptyList(), " Returns {@code} or {@literal}.\n");

        assertEquals("Returns <code></code> or .", new CommentFormatter().format(doc.getComment()));
        assertEquals("Returns <code></code> or .", doc.getComment().toString());
    }

    // https://github.com/dnault/therapi-runtime-javadoc/issues/75
    @Test
    public void givenParamTagsAlignedByFormatter_expectNamesAndComments() {
        // As aligned by the Eclipse formatter in the issue
        String javadoc = " @param  param1 first paramater\n @param  param2 second paramater\n @return        the data\n";

        MethodJavadoc doc = JavadocParser.parseMethodJavadoc(
            "com.example.Example", "example", Arrays.asList("java.lang.String", "java.lang.String"), javadoc);

        List<ParamJavadoc> params = doc.getParams();
        assertEquals(2, params.size());
        assertEquals("param1", params.get(0).getName());
        assertEquals("first paramater", params.get(0).getComment().toString());
        assertEquals("param2", params.get(1).getName());
        assertEquals("second paramater", params.get(1).getComment().toString());
        assertEquals("the data", doc.getReturns().toString());
    }

    @Test
    public void givenParamTagsSeparatedByTabsOrNamingTypeParameters_expectNamesAndComments() {
        String javadoc = " @param\t\tvalue the value\n @param  <T>  the type\n";

        MethodJavadoc doc = JavadocParser.parseMethodJavadoc(
            "com.example.Example", "example", Collections.singletonList("java.lang.Object"), javadoc);

        List<ParamJavadoc> params = doc.getParams();
        assertEquals(2, params.size());
        assertEquals("value", params.get(0).getName());
        assertEquals("the value", params.get(0).getComment().toString());
        assertEquals("<T>", params.get(1).getName());
        assertEquals("the type", params.get(1).getComment().toString());
    }

    @Test
    public void givenSeeTagsAlignedByFormatter_expectAllKindsParsed() {
        String javadoc = " @see  \"Some text\"\n @see  <a href=\"https://example.com\">example</a>\n @see  String#trim()\n";

        MethodJavadoc doc = JavadocParser.parseMethodJavadoc(
            "com.example.Example", "example", Collections.emptyList(), javadoc);

        List<SeeAlsoJavadoc> seeAlso = doc.getSeeAlso();
        assertEquals(3, seeAlso.size());

        assertEquals(SeeAlsoType.STRING_LITERAL, seeAlso.get(0).getSeeAlsoType());
        assertEquals("Some text", seeAlso.get(0).getStringLiteral());

        assertEquals(SeeAlsoType.HTML_LINK, seeAlso.get(1).getSeeAlsoType());
        assertEquals("example", seeAlso.get(1).getHtmlLink().getText());
        assertEquals("https://example.com", seeAlso.get(1).getHtmlLink().getLink());

        assertEquals(SeeAlsoType.JAVADOC_LINK, seeAlso.get(2).getSeeAlsoType());
        assertEquals("String", seeAlso.get(2).getLink().getReferencedClassName());
        assertEquals("trim", seeAlso.get(2).getLink().getReferencedMemberName());
    }
}
