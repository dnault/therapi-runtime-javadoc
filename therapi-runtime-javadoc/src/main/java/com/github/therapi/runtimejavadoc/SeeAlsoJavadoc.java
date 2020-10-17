package com.github.therapi.runtimejavadoc;

/**
 * Represents a {@code @see} tag on a class or method.
 */
public class SeeAlsoJavadoc {

    public enum SeeAlsoType {
        STRING_LITERAL, HTML_LINK, JAVADOC_LINK
    }

    private final SeeAlsoType seeAlsoType;
    private final String stringLiteral;
    private final HtmlLink htmlLink;
    private final Link link;

    public SeeAlsoJavadoc(String stringLiteral) {
        this(SeeAlsoType.STRING_LITERAL, stringLiteral, null, null);
    }

    public SeeAlsoJavadoc(HtmlLink htmlLink) {
        this(SeeAlsoType.HTML_LINK, null, htmlLink, null);
    }

    public SeeAlsoJavadoc(Link link) {
        this(SeeAlsoType.JAVADOC_LINK, null, null, link);
    }

    private SeeAlsoJavadoc(SeeAlsoType seeAlsoType, String stringLiteral, HtmlLink htmlLink, Link link) {
        this.seeAlsoType = seeAlsoType;
        this.stringLiteral = stringLiteral;
        this.htmlLink = htmlLink;
        this.link = link;
    }

    public SeeAlsoType getSeeAlsoType() {
        return seeAlsoType;
    }

    public String getStringLiteral() {
        return stringLiteral;
    }

    public HtmlLink getHtmlLink() {
        return htmlLink;
    }

    public Link getLink() {
        return link;
    }

    public static class HtmlLink {
        private final String text;
        private final String link;

        public HtmlLink(String text, String link) {
            this.text = text;
            this.link = link;
        }

        public String getText() {
            return text;
        }

        public String getLink() {
            return link;
        }
    }
}