package com.github.therapi.runtimejavadoc.internal;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.ClassResolver;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.Import;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.internal.parser.ImportParser;
import com.github.therapi.runtimejavadoc.internal.parser.JavadocParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.constructorsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.fieldsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.importsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;

public class JsonJavadocReader {

    public static ClassJavadoc readClassJavadoc(String qualifiedClassName, JsonObject json) {
        String              className          = qualifiedClassName.replace("$", ".");
        String              packageRef         = extractPackageFrom(qualifiedClassName);
        List<Import>        imports            = readImports(json.get(importsFieldName()));
        ClassResolver       classResolver      = ClassResolver.createClassResolverFor( packageRef, imports );
        List<FieldJavadoc>  fields             = readFieldDocs(qualifiedClassName, classResolver, json.get(fieldsFieldName()));
        List<FieldJavadoc>  enumConstants      = readFieldDocs(qualifiedClassName, classResolver, json.get(enumConstantsFieldName()));
        List<MethodJavadoc> methods            = readMethodDocs(qualifiedClassName, classResolver, json.get(methodsFieldName()));
        List<MethodJavadoc> constructors       = readMethodDocs(qualifiedClassName, classResolver, json.get(constructorsFieldName()));
        String              classJavadocString = json.getString(elementDocFieldName(), null);

        return JavadocParser.parseClassJavadoc(className, classResolver, classJavadocString, fields, enumConstants, methods, constructors);
    }

    private static String extractPackageFrom( String qualifiedClassName ) {
        int i = qualifiedClassName.lastIndexOf( '.' );
        if ( i < 0 ) {
            return null;
        }

        return qualifiedClassName.substring( 0, i );
    }

    private static List<Import> readImports( JsonValue jsonValue ) {
        if (jsonValue == null) {
            // old versions might not have this JSON field
            return Collections.emptyList();
        }

        JsonArray    fieldsArray = jsonValue.asArray();
        List<Import> imports     = new ArrayList<>(fieldsArray.size());

        for (JsonValue fieldValue : fieldsArray) {
            Import parsedImport = ImportParser.parseImport( fieldValue.asString() );

            if ( parsedImport != null ) {
                imports.add( parsedImport );
            }
        }

        return imports;
    }

    private static List<FieldJavadoc> readFieldDocs(String owningClass, ClassResolver classResolver, JsonValue fieldsValue) {
        if (fieldsValue == null) {
            // old versions might not have this JSON field
            return Collections.emptyList();
        }

        JsonArray          fieldsArray = fieldsValue.asArray();
        List<FieldJavadoc> fields      = new ArrayList<>(fieldsArray.size());

        for (JsonValue fieldValue : fieldsArray) {
            fields.add(readFieldDoc(owningClass, classResolver, fieldValue));
        }

        return fields;
    }

    private static FieldJavadoc readFieldDoc(String owningClass, ClassResolver classResolver, JsonValue fieldValue) {
        JsonObject field     = fieldValue.asObject();
        String     fieldName = field.getString(elementNameFieldName(), null);
        String     fieldDoc  = field.getString(elementDocFieldName(), null);

        return JavadocParser.parseFieldJavadoc(owningClass, classResolver, fieldName, fieldDoc);
    }

    private static List<MethodJavadoc> readMethodDocs(String owningClass, ClassResolver classResolver, JsonValue methodsValue) {
        JsonArray methodArray = methodsValue.asArray();
        List<MethodJavadoc> methods = new ArrayList<>(methodArray.size());
        for (JsonValue methodValue : methodArray) {
            methods.add(readMethodDoc(owningClass, classResolver, methodValue));
        }
        return methods;
    }

    private static MethodJavadoc readMethodDoc(String owningClass, ClassResolver classResolver, JsonValue methodValue) {
        JsonObject method = methodValue.asObject();
        String methodName = method.getString(elementNameFieldName(), null);
        List<String> paramTypes = readParamTypes(method.get(paramTypesFieldName()));
        String methodDoc = method.getString(elementDocFieldName(), null);
        return JavadocParser.parseMethodJavadoc(owningClass, classResolver, methodName, paramTypes, methodDoc);
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
