package com.github.therapi.runtimejavadoc.internal.parser;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.InlineTag;
import com.github.therapi.runtimejavadoc.Link;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class CommentParserTest {

    @DataPoints
    public static String[] textOnlyData() {
        return new String[] {
                "", " ", "\t\n", "abcdef", "abc def\nxyz", "abc}def"
        };
    }

    @Theory
    public void parse_textOnly(String text) {
        List<CommentElement> elements = CommentParser.parse(text).getElements();
        assertEquals(1, elements.size());
        assertEquals(new CommentText(text), elements.get(0));
    }

    @Test
    public void parse_linkOnly_simpleLink() {
        List<CommentElement> elements = CommentParser.parse("{@link ClassName}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(0));
    }

    @Test
    public void parse_linkOnly_labeledLink_noWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("{@link ClassName myLabel}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("myLabel", "ClassName", null)), elements.get(0));
    }

    @Test
    public void parse_linkOnly_labeledLink_withWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("{@link ClassName my label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("my label", "ClassName", null)), elements.get(0));
    }

    @Test
    public void parse_linkOnly_linkWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("{@link ClassName#member}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("ClassName#member", "ClassName", "member")), elements.get(0));
    }

    @Test
    public void parse_linkOnly_labeledLinkWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("{@link ClassName#member label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "ClassName", "member")), elements.get(0));
    }

    @Test
    public void parse_tagOnly_noWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("{@sometag someValue}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineTag("sometag", "someValue"), elements.get(0));
    }

    @Test
    public void parse_tagOnly_withWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("{@sometag some value}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineTag("sometag", "some value"), elements.get(0));
    }

    @Test
    public void parse_mix_textAndTag() {
        List<CommentElement> elements = CommentParser.parse("text before {@sometag some value}").getElements();
        assertEquals(2, elements.size());
        assertEquals(new CommentText("text before "), elements.get(0));
        assertEquals(new InlineTag("sometag", "some value"), elements.get(1));
    }

    @Test
    public void parse_mix_textAndLink() {
        List<CommentElement> elements = CommentParser.parse("text before {@link ClassName} text after").getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("text before "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(1));
        assertEquals(new CommentText(" text after"), elements.get(2));
    }

    @Test
    public void parse_mix_textAndLink_withWeirdBraces() {
        List<CommentElement> elements = CommentParser.parse("text}before {@link ClassName} text{after").getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("text}before "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(1));
        assertEquals(new CommentText(" text{after"), elements.get(2));
    }
}
