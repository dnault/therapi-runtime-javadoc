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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ToTextListCommentVisitor implements CommentVisitor {
    protected List<String> lines = new ArrayList<>();

    public void commentText(String value) {
        String[] splits = value.split("\r\n|\n");

        lines.addAll(Arrays.asList(splits));
    }

    public void inlineLink(Link link) {
        appendInlineText(link.toString());
    }

    public void inlineTag(String name, String value) {
        appendInlineText(value);
    }

    public void inlineValue(Value value) {
        appendInlineText(value.toString());
    }

    public List<String> build() {
        return lines;
    }

    private void appendInlineText(String txt) {
        if (txt == null) {
        } else if (lines.isEmpty()) {
            lines.add(txt);
        } else {
            int index = lines.size() - 1;
            String newLine = lines.get(index) + txt;

            lines.set(index, newLine);
        }
    }
}
