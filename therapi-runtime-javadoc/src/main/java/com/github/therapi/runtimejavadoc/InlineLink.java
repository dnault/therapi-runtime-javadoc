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

import java.util.Objects;

public class InlineLink extends CommentElement {
    private final Link link;

    public InlineLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public void visit(CommentVisitor visitor) {
        visitor.inlineLink(link);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineLink that = (InlineLink) o;
        return Objects.equals(link, that.link);
    }

    @Override
    public String toString() {
        return "link=" + link;
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
