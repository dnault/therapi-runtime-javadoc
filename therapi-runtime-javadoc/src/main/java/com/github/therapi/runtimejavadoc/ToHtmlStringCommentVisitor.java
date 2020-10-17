package com.github.therapi.runtimejavadoc;


class ToHtmlStringCommentVisitor implements CommentVisitor {
    public StringBuilder buf = new StringBuilder();

    public void commentText( String value ) {
        buf.append( value );
    }

    public void inlineLink( Link link ) {
        buf.append("{@link ");
        buf.append(link);
        buf.append("}");
    }

    public void inlineTag( String name, String value ) {
        if ("code".equals(name)) {
            buf.append( "<code>" );
            buf.append( escapeHtml( value ) );
            buf.append( "</code>" );
        } else if ("literal".equals(name)) {
            buf.append(escapeHtml(value));
        } else {
            buf.append("{@");
            buf.append(name);
            buf.append(" ");
            buf.append(value);
            buf.append("}");
        }
    }

    public void inlineValue( Value value ) {
        if (value.getReferencedMemberName() == null) {
            buf.append("{@value}");
        } else {
            buf.append("{@value ");
            buf.append(value);
            buf.append("}");
        }
    }

    public String build() {
        return buf.toString();
    }

    /**
     * Escapes the HTML special characters: {@code " & < >}
     *
     * @param value The value to escape
     * @return the input value with any instances of HTML special characters converted to character entities.
     */
    protected static String escapeHtml(String value) {
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
