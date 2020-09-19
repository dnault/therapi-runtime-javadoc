package com.github.therapi.runtimejavadoc;

/**
 * Performs basic conversion of a Comment into a String. Subclasses are encouraged
 * to override the {@link #renderLink} method to convert {@link InlineLink}s to hyperlinks.
 * <p>
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

        ToHtmlStringCommentVisitor visitor = new ToHtmlStringCommentVisitor();
        comment.visit( visitor );

        return visitor.build();
    }
}
