package com.github.therapi.runtimejavadoc.internal.parser;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.InlineTag;
import com.github.therapi.runtimejavadoc.Link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Theories.class)
public class CommentParserTest {

    @DataPoints("null/blank")
    public static String[] nullOrBlank() {
        return new String[] {
                null, "", " ", "   ", "\t\n", "  \n  "
        };
    }

    @DataPoints("non-blank")
    public static String[] nonBlankTextOnly() {
        return new String[] {
                "abcdef", "abc def\nxyz", "abc}def", "{abc}def"
        };
    }

    @Theory
    public void parse_absentCommment_shouldBeNull(@FromDataPoints("null/blank") String input) {
        assertNull(CommentParser.parse("TestClass", input));
    }

    @Theory
    public void parse_textOnly_shouldBeSingleTextNode(@FromDataPoints("non-blank") String input) {
        List<CommentElement> elements = CommentParser.parse("TestClass", input).getElements();
        assertEquals(1, elements.size());
        assertEquals(new CommentText(input), elements.get(0));
    }

    @Test
    public void parse_linkOnly_simpleLink() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(0));
    }

    @Test
    public void parse_linkOnly_labeledLink_noWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName myLabel}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("myLabel", "ClassName", null)), elements.get(0));
    }

    @Test
    public void parse_linkOnly_labeledLink_withWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName my label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("my label", "ClassName", null)), elements.get(0));
    }

    @Test
    public void parse_linkOnly_linkWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName#member}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("ClassName#member", "ClassName", "member")), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName#member label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "ClassName", "member")), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMemberRef_ImplicitRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link #member label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "TestClass", "member")), elements.get(0));
    }

    @Test
    public void parse_tagOnly_noWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@sometag someValue}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineTag("sometag", "someValue"), elements.get(0));
    }

    @Test
    public void parse_tagOnly_withWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@sometag some value}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineTag("sometag", "some value"), elements.get(0));
    }

    @Test
    public void parse_tagOnly_emptyTag() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@sometag}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineTag("sometag", null), elements.get(0));
    }

    @Test
    public void parse_tagOnly_whitespaceTag() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@sometag  \t }").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineTag("sometag", null), elements.get(0));
    }

    @Test
    public void parse_mix_textAndTag() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "text before {@sometag some value}").getElements();
        assertEquals(2, elements.size());
        assertEquals(new CommentText("text before "), elements.get(0));
        assertEquals(new InlineTag("sometag", "some value"), elements.get(1));
    }

    @Test
    public void parse_mix_textAndLink() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "text before {@link ClassName} text after").getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("text before "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(1));
        assertEquals(new CommentText(" text after"), elements.get(2));
    }

    @Test
    public void parse_mix_textAndLink_realLife() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "Adds the given {@link Action}s to the queue.")
                                                     .getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("Adds the given "), elements.get(0));
        assertEquals(new InlineLink(new Link("Action", "Action", null)), elements.get(1));
        assertEquals(new CommentText("s to the queue."), elements.get(2));
    }

    @Test
    public void parse_mix_textAndLink_withWeirdBraces() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "text}before {@link ClassName} text{after").getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("text}before "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(1));
        assertEquals(new CommentText(" text{after"), elements.get(2));
    }

    @Test
    public void parse_mix_praiseTheMightyThor() {
        String input = "  text}bef{}ore {@link ClassName}{@} text{after}\nand {@empty}\n\n";
        List<CommentElement> elements = CommentParser.parse("TestClass", input).getElements();
        assertEquals(4, elements.size());
        assertEquals(new CommentText("text}bef{}ore "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null)), elements.get(1));
        assertEquals(new CommentText("{@} text{after}\nand "), elements.get(2));
        assertEquals(new InlineTag("empty", null), elements.get(3));
    }
}
