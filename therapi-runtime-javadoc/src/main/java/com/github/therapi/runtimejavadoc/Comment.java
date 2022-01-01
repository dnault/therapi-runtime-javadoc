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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

/**
 * Comment text that may contain inline tags.
 */
public class Comment implements Iterable<CommentElement> {
    private static final Comment EMPTY = new Comment(Collections.<CommentElement>emptyList());

    public static Comment createEmpty() {
        return EMPTY;
    }

    public static Comment nullToEmpty(Comment c) {
        return c == null ? EMPTY : c;
    }

    private final List<CommentElement> elements;

    public Comment(List<CommentElement> elements) {
        this.elements = unmodifiableDefensiveCopy(elements);
    }

    /**
     * Returns the elements this comment consists of.
     *
     * @return the elements this comment consists of.
     */
    public List<CommentElement> getElements() {
        return elements;
    }

    @Override
    public Iterator<CommentElement> iterator() {
        return elements.iterator();
    }

    public void visit(CommentVisitor visitor) {
        for (CommentElement e : elements) {
            e.visit(visitor);
        }
    }

    @Override
    public String toString() {
        return new CommentFormatter().format(this);
    }
}
