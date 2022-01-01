/*
 * Copyright 2015 David Nault and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.therapi.runtimejavadoc;

/**
 * Performs basic conversion of a Comment into a String.
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
        comment.visit(visitor);

        return visitor.build();
    }
}
