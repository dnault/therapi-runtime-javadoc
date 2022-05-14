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

package com.github.therapi.runtimejavadoc.scribe;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.constructorsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.fieldsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.isBlank;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

class JsonJavadocBuilder {
    private final ProcessingEnvironment processingEnv;

    JsonJavadocBuilder(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    private static JsonArray getJavadocsAsJson(List<Element> elements, ElementToJsonFunction createDoc) {
        JsonArray jsonArray = new JsonArray();
        for (Element e : elements) {
            JsonObject eMapped = createDoc.apply(e);
            if (eMapped != null) {
                jsonArray.add(eMapped);
            }
        }
        return jsonArray;
    }

    private static <T> T defaultIfNull(T actualValue, T defaultValue) {
        return actualValue != null ? actualValue : defaultValue;
    }

    JsonObject getClassJavadocAsJsonOrNull(TypeElement classElement) {
        String classDoc = processingEnv.getElementUtils().getDocComment(classElement);

        if (isBlank(classDoc)) {
            classDoc = "";
        }

        Map<ElementKind, List<Element>> children = new HashMap<>();
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            children.computeIfAbsent(enclosedElement.getKind(), k -> new ArrayList<>()).add(enclosedElement);
        }

        final List<Element> emptyList = Collections.emptyList();
        List<Element> enclosedFields = defaultIfNull(children.get(FIELD), emptyList);
        List<Element> enclosedEnumConstants = defaultIfNull(children.get(ENUM_CONSTANT), emptyList);
        List<Element> enclosedMethods = defaultIfNull(children.get(METHOD), emptyList);
        List<Element> enclosedConstructors = defaultIfNull(children.get(CONSTRUCTOR), emptyList);

        JsonArray fieldDocs = getJavadocsAsJson(enclosedFields, new FieldJavadocAsJson());
        JsonArray enumConstantDocs = getJavadocsAsJson(enclosedEnumConstants, new FieldJavadocAsJson());
        JsonArray methodDocs = getJavadocsAsJson(enclosedMethods, new MethodJavadocAsJson());
        JsonArray constructorDocs = getJavadocsAsJson(enclosedConstructors, new MethodJavadocAsJson());

        if (isBlank(classDoc)
            && fieldDocs.isEmpty()
            && enumConstantDocs.isEmpty()
            && methodDocs.isEmpty()
            && constructorDocs.isEmpty()) {
            return null;
        }

        JsonObject json = new JsonObject();
        json.add(elementDocFieldName(), classDoc);
        json.add(fieldsFieldName(), fieldDocs);
        json.add(enumConstantsFieldName(), enumConstantDocs);
        json.add(methodsFieldName(), methodDocs);
        json.add(constructorsFieldName(), constructorDocs);
        return json;
    }

    private JsonArray getParamErasures(ExecutableElement executableElement) {
        Types typeUtils = processingEnv.getTypeUtils();

        final JsonArray jsonValues = new JsonArray();
        for (VariableElement parameter : executableElement.getParameters()) {
            TypeMirror erasure = typeUtils.erasure(parameter.asType());
            String typeName = TypeName.get(erasure).withoutAnnotations().toString();
            jsonValues.add(Json.value(typeName));
        }
        return jsonValues;
    }

    private interface ElementToJsonFunction {
        // nullable
        JsonObject apply(Element e);
    }

    private class FieldJavadocAsJson implements ElementToJsonFunction {
        @Override
        public JsonObject apply(Element field) {
            String javadoc = processingEnv.getElementUtils().getDocComment(field);
            if (isBlank(javadoc)) {
                return null;
            }

            JsonObject jsonDoc = new JsonObject();
            jsonDoc.add(elementNameFieldName(), field.getSimpleName().toString());
            jsonDoc.add(elementDocFieldName(), javadoc);
            return jsonDoc;
        }
    }

    private class MethodJavadocAsJson implements ElementToJsonFunction {

        @Override
        public JsonObject apply(Element method) {
            assert method instanceof ExecutableElement;

            String methodJavadoc = processingEnv.getElementUtils().getDocComment(method);
            if (!isBlank(methodJavadoc)) {
                JsonObject jsonDoc = new JsonObject();
                jsonDoc.add(elementNameFieldName(), method.getSimpleName().toString());
                jsonDoc.add(paramTypesFieldName(), getParamErasures((ExecutableElement) method));
                jsonDoc.add(elementDocFieldName(), methodJavadoc);
                return jsonDoc;
            }

            Element enclosingElement = method.getEnclosingElement();
            TypeMirror enclosingType = enclosingElement.asType();
            if (enclosingType.getKind() != TypeKind.DECLARED) {
                return null;
            }

            JsonObject jsonDoc = getOverriddenElementJavadoc((ExecutableElement) method, (DeclaredType) enclosingType,
                                                             (TypeElement) enclosingElement);
            if (jsonDoc != null) {
                // Override the paramTypes to account for generic type erasure
                jsonDoc.set(paramTypesFieldName(), getParamErasures((ExecutableElement) method));
            }
            return jsonDoc;
        }

        private JsonObject getOverriddenElementJavadoc(ExecutableElement method, DeclaredType searchedType, TypeElement overridingElement) {
            List<? extends TypeMirror> extendedElements = processingEnv.getTypeUtils().directSupertypes(searchedType);

            for (TypeMirror extendedElement : extendedElements) {
                if (extendedElement.getKind() != TypeKind.DECLARED) {
                    continue;
                }

                JsonObject jsonDoc = searchForOverriddenJavadoc(method, (DeclaredType) extendedElement, overridingElement);
                if (jsonDoc != null) {
                    return jsonDoc;
                }
            }

            return null;
        }

        private JsonObject searchForOverriddenJavadoc(ExecutableElement method, DeclaredType superType, TypeElement overridingElement) {
            Element superElement = superType.asElement();

            for (Element element : superElement.getEnclosedElements()) {
                if (element.getKind() != METHOD) {
                    continue;
                }

                if (!processingEnv.getElementUtils()
                                 .overrides(method, (ExecutableElement) element,
                                            overridingElement)) {
                    continue;
                }

                JsonObject jsonDoc = this.apply(element);
                if (jsonDoc != null) {
                    return jsonDoc;
                }
            }

            TypeMirror superEnclosure = processingEnv.getTypeUtils().erasure(superElement.asType());
            if (superEnclosure.getKind() != TypeKind.DECLARED) {
                return null;
            }

            return getOverriddenElementJavadoc(method, (DeclaredType) superEnclosure, overridingElement);
        }
    }
}
