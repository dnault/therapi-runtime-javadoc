package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.CommentElement;
import com.github.therapi.runtimejavadoc.CommentText;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.InlineTag;
import com.github.therapi.runtimejavadoc.InlineValue;
import com.github.therapi.runtimejavadoc.Link;
import com.github.therapi.runtimejavadoc.Value;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

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
    public void parse_absentCommment_shouldBeEmpty(@FromDataPoints("null/blank") String input) {
        assertEquals(emptyList(), CommentParser.parse("TestClass", input).getElements());
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
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null, null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_simpleLink_FullyQualified() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link org.company.ClassName}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("org.company.ClassName", "org.company.ClassName", null, null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLink_noWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName myLabel}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("myLabel", "ClassName", null, null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLink_withWhiteSpace() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName my label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("my label", "ClassName", null, null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_linkWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName#member}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("ClassName#member", "ClassName", "member", null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link ClassName#member label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "ClassName", "member", null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMemberRef_ImplicitRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link #member label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "TestClass", "member", null)), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMethodMemberRef_ImplicitRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link #withRecipientsWithDefaultName(String, Collection, RecipientType) label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "TestClass", "withRecipientsWithDefaultName",
                asList("String", "Collection", "RecipientType"))), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMethodMemberRef_WithParams() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link TestClass2#withRecipientsWithDefaultName(String, Collection, RecipientType) label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "TestClass2", "withRecipientsWithDefaultName",
                asList("String", "Collection", "RecipientType"))), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMethodMemberRef_ImplicitRef_NoParameters() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link #withRecipientsWithDefaultName() label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "TestClass", "withRecipientsWithDefaultName", Collections.<String>emptyList())), elements.get(0));
    }
    
    @Test
    public void parse_linkOnly_labeledLinkWithMethodMemberRef_NoParameters() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@link TestClass2#withRecipientsWithDefaultName() label}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineLink(new Link("label", "TestClass2", "withRecipientsWithDefaultName", Collections.<String>emptyList())), elements.get(0));
    }
    
    @Test
    public void parse_valueOnly_valueWithMemberRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@value ClassName#member}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineValue(new Value("ClassName", "member")), elements.get(0));
    }
    
    @Test
    public void parse_valueOnly_valueWithFullyQualifiedClassAndMemberRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@value a.b.c.ClassName#member}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineValue(new Value("a.b.c.ClassName", "member")), elements.get(0));
    }
    
    @Test
    public void parse_valueOnly_valueWithMemberRef_ImplicitRef() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "{@value #member}").getElements();
        assertEquals(1, elements.size());
        assertEquals(new InlineValue(new Value("TestClass", "member")), elements.get(0));
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
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null, null)), elements.get(1));
        assertEquals(new CommentText(" text after"), elements.get(2));
    }
    
    @Test
    public void parse_mix_textAndValue() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "text before {@value #member} text after").getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("text before "), elements.get(0));
        assertEquals(new InlineValue(new Value("TestClass", "member")), elements.get(1));
        assertEquals(new CommentText(" text after"), elements.get(2));
    }
    
    @Test
    public void parse_mix_textAndValueAndLink() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "text before {@value #member1} text after {@link ClassName#member2} more text after").getElements();
        assertEquals(5, elements.size());
        assertEquals(new CommentText("text before "), elements.get(0));
        assertEquals(new InlineValue(new Value("TestClass", "member1")), elements.get(1));
        assertEquals(new CommentText(" text after "), elements.get(2));
        assertEquals(new InlineLink(new Link("ClassName#member2", "ClassName", "member2", null)), elements.get(3));
        assertEquals(new CommentText(" more text after"), elements.get(4));
    }

    @Test
    public void parse_mix_textAndLink_realLife() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "Adds the given {@link Action}s to the queue.")
                                                     .getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("Adds the given "), elements.get(0));
        assertEquals(new InlineLink(new Link("Action", "Action", null, null)), elements.get(1));
        assertEquals(new CommentText("s to the queue."), elements.get(2));
    }

    @Test
    public void parse_mix_textAndLink_withWeirdBraces() {
        List<CommentElement> elements = CommentParser.parse("TestClass", "text}before {@link ClassName} text{after").getElements();
        assertEquals(3, elements.size());
        assertEquals(new CommentText("text}before "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null, null)), elements.get(1));
        assertEquals(new CommentText(" text{after"), elements.get(2));
    }

    @Test
    public void parse_mix_praiseTheMightyThor() {
        String input = "  text}bef{}ore {@link ClassName}{@} text{after}\nand {@empty}{@value #member}\n\n";
        List<CommentElement> elements = CommentParser.parse("TestClass", input).getElements();
        assertEquals(5, elements.size());
        assertEquals(new CommentText("text}bef{}ore "), elements.get(0));
        assertEquals(new InlineLink(new Link("ClassName", "ClassName", null, null)), elements.get(1));
        assertEquals(new CommentText("{@} text{after}\nand "), elements.get(2));
        assertEquals(new InlineTag("empty", null), elements.get(3));
        assertEquals(new InlineValue(new Value("TestClass", "member")), elements.get(4));
    }
}
