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


public class ParamJavadoc {
    private final String name;
    private final Comment comment;

    public ParamJavadoc(String name, Comment comment) {
        this.name = name;
        this.comment = comment;
    }

    /**
     * Returns the name of parameter
     *
     * @return the name of parameter
     */
    public String getName() {
        return name;
    }

    public Comment getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return name + " " + comment;
    }
}
