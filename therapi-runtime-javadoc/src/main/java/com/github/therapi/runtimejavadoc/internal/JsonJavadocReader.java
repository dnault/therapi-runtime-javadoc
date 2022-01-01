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

package com.github.therapi.runtimejavadoc.internal;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.internal.parser.JavadocParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.constructorsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.fieldsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;

public class JsonJavadocReader {

    public static ClassJavadoc readClassJavadoc(String qualifiedClassName, JsonObject json) {
        String className = qualifiedClassName.replace("$", ".");
        List<FieldJavadoc> fields = readFieldDocs(qualifiedClassName, json.get(fieldsFieldName()));
        List<FieldJavadoc> enumConstants = readFieldDocs(qualifiedClassName, json.get(enumConstantsFieldName()));
        List<MethodJavadoc> methods = readMethodDocs(qualifiedClassName, json.get(methodsFieldName()));
        List<MethodJavadoc> constructors = readMethodDocs(qualifiedClassName, json.get(constructorsFieldName()));
        String classJavadocString = json.getString(elementDocFieldName(), null);
        return JavadocParser.parseClassJavadoc(className, classJavadocString, fields, enumConstants, methods, constructors);
    }

    private static List<FieldJavadoc> readFieldDocs(String owningClass, JsonValue fieldsValue) {
        if (fieldsValue == null) {
            // old versions might not have this JSON field
            return Collections.emptyList();
        }
        JsonArray fieldsArray = fieldsValue.asArray();
        List<FieldJavadoc> fields = new ArrayList<>(fieldsArray.size());
        for (JsonValue fieldValue : fieldsArray) {
            fields.add(readFieldDoc(owningClass, fieldValue));
        }
        return fields;
    }

    private static FieldJavadoc readFieldDoc(String owningClass, JsonValue fieldValue) {
        JsonObject field = fieldValue.asObject();
        String fieldName = field.getString(elementNameFieldName(), null);
        String fieldDoc = field.getString(elementDocFieldName(), null);
        return JavadocParser.parseFieldJavadoc(owningClass, fieldName, fieldDoc);
    }

    private static List<MethodJavadoc> readMethodDocs(String owningClass, JsonValue methodsValue) {
        JsonArray methodArray = methodsValue.asArray();
        List<MethodJavadoc> methods = new ArrayList<>(methodArray.size());
        for (JsonValue methodValue : methodArray) {
            methods.add(readMethodDoc(owningClass, methodValue));
        }
        return methods;
    }

    private static MethodJavadoc readMethodDoc(String owningClass, JsonValue methodValue) {
        JsonObject method = methodValue.asObject();
        String methodName = method.getString(elementNameFieldName(), null);
        List<String> paramTypes = readParamTypes(method.get(paramTypesFieldName()));
        String methodDoc = method.getString(elementDocFieldName(), null);
        return JavadocParser.parseMethodJavadoc(owningClass, methodName, paramTypes, methodDoc);
    }

    private static List<String> readParamTypes(JsonValue paramTypesValue) {
        JsonArray paramTypesArray = paramTypesValue.asArray();
        List<String> paramTypes = new ArrayList<>(paramTypesArray.size());
        for (JsonValue v : paramTypesArray) {
            paramTypes.add(v.asString());
        }
        return paramTypes;
    }
}
