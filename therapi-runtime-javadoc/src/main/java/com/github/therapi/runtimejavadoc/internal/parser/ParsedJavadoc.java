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

package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;
import java.util.ArrayList;
import java.util.List;

class ParsedJavadoc {

    private final Comment description;
    private final List<OtherJavadoc> otherDocs;
    private final List<SeeAlsoJavadoc> seeAlsoDocs;
    private final List<ParamJavadoc> paramDocs;
    private final List<ThrowsJavadoc> throwsDocs;
    private final Comment returns;

    ParsedJavadoc(Comment description, List<OtherJavadoc> otherDocs, List<SeeAlsoJavadoc> seeAlsoDocs,
                         List<ParamJavadoc> paramDocs, List<ThrowsJavadoc> throwsDocs, Comment returns) {
        this.description = description;
        this.otherDocs = unmodifiableDefensiveCopy(otherDocs);
        this.seeAlsoDocs = unmodifiableDefensiveCopy(seeAlsoDocs);
        this.paramDocs = unmodifiableDefensiveCopy(paramDocs);
        this.throwsDocs = unmodifiableDefensiveCopy(throwsDocs);
        this.returns = Comment.nullToEmpty(returns);
    }

    @Override
    public String toString() {
        return "ParsedJavadoc{" +
                "description='" + description + '\'' +
                ", otherDocs=" + otherDocs +
                ", seeAlsoDocs=" + seeAlsoDocs +
                ", paramDocs=" + paramDocs +
                ", throwsDocs=" + throwsDocs +
                ", returns=" + returns +
                '}';
    }

    Comment getDescription() {
        return description;
    }

    List<OtherJavadoc> getOtherDocs() {
        return otherDocs;
    }

    List<SeeAlsoJavadoc> getSeeAlsoDocs() {
        return seeAlsoDocs;
    }

    List<ParamJavadoc> getParamDocs() {
        return paramDocs;
    }

    List<ThrowsJavadoc> getThrowsDocs() {
        return throwsDocs;
    }

    Comment getReturns() {
        return returns;
    }
}
