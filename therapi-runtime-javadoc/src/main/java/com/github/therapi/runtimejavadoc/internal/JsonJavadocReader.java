package com.github.therapi.runtimejavadoc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.internal.parser.JavadocParser;

import javax.annotation.Nullable;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.fieldsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;

public class JsonJavadocReader {

    @Nullable
    public static ClassJavadoc readClassJavadoc(String qualifiedClassName, JsonObject json) {
        String className = qualifiedClassName.replace("$", ".");
        List<FieldJavadoc> fields = readFieldDocs(json.get(fieldsFieldName()));
        List<FieldJavadoc> enumConstants = readFieldDocs(json.get(enumConstantsFieldName()));
        List<MethodJavadoc> methods = readMethodDocs(json.get(methodsFieldName()));
        String classJavadocString = json.getString(elementDocFieldName(), null);
        return JavadocParser.parseClassJavadoc(className, classJavadocString, fields, enumConstants, methods);
    }

    private static List<FieldJavadoc> readFieldDocs(JsonValue fieldsValue) {
        if (fieldsValue == null) {
            // old versions might not have this JSON field
            return Collections.emptyList();
        }
        JsonArray fieldsArray = fieldsValue.asArray();
        List<FieldJavadoc> fields = new ArrayList<>(fieldsArray.size());
        for (JsonValue fieldValue : fieldsArray) {
            fields.add(readFieldDoc(fieldValue));
        }
        return fields;
    }

    private static FieldJavadoc readFieldDoc(JsonValue fieldValue) {
        JsonObject field = fieldValue.asObject();
        String fieldName = field.getString(elementNameFieldName(), null);
        String fieldDoc = field.getString(elementDocFieldName(), null);
        return JavadocParser.parseFieldJavadoc(fieldName, fieldDoc);
    }

    private static List<MethodJavadoc> readMethodDocs(JsonValue methodsValue) {
        JsonArray methodArray = methodsValue.asArray();
        List<MethodJavadoc> methods = new ArrayList<>(methodArray.size());
        for (JsonValue methodValue : methodArray) {
            methods.add(readMethodDoc(methodValue));
        }
        return methods;
    }

    private static MethodJavadoc readMethodDoc(JsonValue methodValue) {
        JsonObject method = methodValue.asObject();
        String methodName = method.getString(elementNameFieldName(), null);
        List<String> paramTypes = readParamTypes(method.get(paramTypesFieldName()));
        String methodDoc = method.getString(elementDocFieldName(), null);
        return JavadocParser.parseMethodJavadoc(methodName, paramTypes, methodDoc);
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
