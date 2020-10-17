package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.Link;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc.HtmlLink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class SeeAlsoParser {

    private static final Pattern stringLiteralPattern = compile("^\"(?<string>.*)\"$");
    // https://regex101.com/r/lZmCCx/1
    private static final Pattern htmlLink = compile("(?s)<a\\s*href=['\"](?<link>.+?)['\"]\\s*>(?<text>.+)<\\/a>");

    /**
     * @return null if tag is malformed
     */
    public static SeeAlsoJavadoc parseSeeAlso(String owningClass, String value) {
        SeeAlsoJavadoc seeAlsoJavadoc = parseAsStringLiteral(value);
        if (seeAlsoJavadoc == null) {
            seeAlsoJavadoc = parseAsHtmlLink(value);
        }
        if (seeAlsoJavadoc == null) {
            seeAlsoJavadoc = parseAsJavadocLink(owningClass, value);
        }
        return seeAlsoJavadoc;
    }

    private static SeeAlsoJavadoc parseAsStringLiteral(String value) {
        Matcher stringLiteralMatcher = stringLiteralPattern.matcher(value);
        return stringLiteralMatcher.find() ? new SeeAlsoJavadoc(stringLiteralMatcher.group("string")) : null;
    }

    private static SeeAlsoJavadoc parseAsHtmlLink(String value) {
        Matcher matcher = htmlLink.matcher(value);
        if (matcher.matches()) {
            return new SeeAlsoJavadoc(new HtmlLink(matcher.group("text"), matcher.group("link")));
        }
        return null;
    }

    private static SeeAlsoJavadoc parseAsJavadocLink(String owningClass, String value) {
        Link javadocLink = LinkParser.createLinkElement(owningClass, value);
        return javadocLink == null ? null : new SeeAlsoJavadoc(javadocLink);
    }
}