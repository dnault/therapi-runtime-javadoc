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

public class Value {
    private final String referencedClassName;
    private final String referencedMemberName;

    public Value(String referencedClassName, String referencedMemberName) {
        this.referencedClassName = referencedClassName;
        this.referencedMemberName = referencedMemberName;
    }

    public String getReferencedClassName() {
        return referencedClassName;
    }

    public String getReferencedMemberName() {
        return referencedMemberName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Value link = (Value) o;
        return Objects.equals(referencedClassName, link.referencedClassName)
                && Objects.equals(referencedMemberName, link.referencedMemberName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referencedClassName, referencedMemberName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (referencedClassName != null) {
            sb.append(referencedClassName);
        }
        if (referencedMemberName != null) {
            sb.append('#').append(referencedMemberName);
        }
        return sb.toString();
    }
}
