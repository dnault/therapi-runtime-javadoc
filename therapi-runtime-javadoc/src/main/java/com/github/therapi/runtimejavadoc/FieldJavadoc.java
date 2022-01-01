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

import java.util.List;

public class FieldJavadoc extends BaseJavadoc {

    public FieldJavadoc(String name, Comment comment, List<OtherJavadoc> other, List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
    }

    public static FieldJavadoc createEmpty(String fieldName) {
        return new FieldJavadoc(fieldName, null, null, null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    @Override
    public String toString() {
        return "FieldJavadoc{"
                + "name='" + getName() + '\''
                + ", comment=" + getComment()
                + ", other=" + getOther()
                + ", seeAlso=" + getSeeAlso()
                + '}';
    }
}
