package com.github.therapi.runtimejavadoc;

/**
 * Performs basic conversion of a Comment into a String. Subclasses are encouraged
 * to override the {@link #renderLink} method to convert {@link InlineLink}s to hyperlinks.
 * <p/>
 * Reusable and thread-safe.
 */
public class CommentFormatter {
    /**
     * Render the comment as an HTML String.
     *
     * @param comment the comment to render (may be {@code null} in which case an empty string is returned)
     * @return the HTML String representation of the comment
     */
    public String format(Comment comment) {
        if (comment == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (CommentElement e : comment) {
            if (e instanceof CommentText) {
                sb.append(renderText((CommentText) e));

            } else if (e instanceof InlineLink) {
                sb.append(renderLink((InlineLink) e));

            } else if (e instanceof InlineTag) {
                sb.append(renderTag((InlineTag) e));

            } else {
                sb.append(renderUnrecognized(e));
            }
        }

        return sb.toString();
    }

    protected String renderText(CommentText text) {
        return text.getValue();
    }

    protected String renderLink(InlineLink e) {
        return "{@link " + e.getLink() + "}";
    }

    protected String renderUnrecognized(CommentElement e) {
        return e.toString();
    }

    protected String renderTag(InlineTag e) {
        switch (e.getName()) {
            case "code":
                return renderCode(e);
            case "literal":
                return renderLiteral(e);
            default:
                return renderUnrecognizedTag(e);
        }
    }

    protected String renderCode(InlineTag e) {
        return "<code>" + escapeHtml(e.getValue()) + "</code>";
    }

    protected String renderLiteral(InlineTag e) {
        return escapeHtml(e.getValue());
    }

    protected String renderUnrecognizedTag(InlineTag e) {
        return "{@" + e.getName() + " " + e.getValue() + "}";
    }

    /**
     * Escapes the HTML special characters: {@code " & < >}
     *
     * @param value The value to escape
     * @return the input value with any instances of HTML special characters converted to character entities.
     */
    protected String escapeHtml(String value) {
        StringBuilder escaped = new StringBuilder();

        for (int i = 0, len = value.length(); i < len; i++) {
            final char c = value.charAt(i);

            if (c == '"') {
                escaped.append("&quot;");
            } else if (c == '&') {
                escaped.append("&amp;");
            } else if (c == '<') {
                escaped.append("&lt;");
            } else if (c == '>') {
                escaped.append("&gt;");
            } else {
                escaped.append(c);
            }
        }

        return escaped.toString();
    }
}
