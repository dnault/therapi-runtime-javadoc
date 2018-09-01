package com.github.therapi.runtimejavadoc.internal;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.elementNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.fieldsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;

class JsonJavadocBuilder {

  private static final Collector<JsonValue, JsonArray, JsonArray> JSON_ARRAY_COLLECTOR = Collector.of(JsonArray::new,
      JsonArray::add, (arr1, arr2) -> {
        arr2.forEach(arr1::add);
        return arr1;
      });

  private final ProcessingEnvironment processingEnv;

  JsonJavadocBuilder(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  Optional<JsonObject> getClassJavadocAsJson(TypeElement classElement) {
    String classDoc = processingEnv.getElementUtils().getDocComment(classElement);

    if (isBlank(classDoc)) {
      classDoc = "";
    }

    Map<ElementKind, List<Element>> children = classElement.getEnclosedElements()
        .stream()
        .collect(Collectors.groupingBy(Element::getKind));
    List<Element> enclosedFields = children.getOrDefault(ElementKind.FIELD, Collections.emptyList());
    List<Element> enclosedEnumConstants = children.getOrDefault(ElementKind.ENUM_CONSTANT, Collections.emptyList());
    List<Element> enclosedMethods = children.getOrDefault(ElementKind.METHOD, Collections.emptyList());

    JsonArray fieldDocs = getJavadocsAsJson(enclosedFields, this::getFieldJavadocAsJson);
    JsonArray enumConstantDocs = getJavadocsAsJson(enclosedEnumConstants, this::getFieldJavadocAsJson);
    JsonArray methodDocs = getJavadocsAsJson(enclosedMethods, this::getMethodJavadocAsJson);

    if (isBlank(classDoc) && fieldDocs.isEmpty() && enumConstantDocs.isEmpty() && methodDocs.isEmpty()) {
      return Optional.empty();
    }

    JsonObject json = new JsonObject();
    json.add(elementDocFieldName(), classDoc);
    json.add(fieldsFieldName(), fieldDocs);
    json.add(enumConstantsFieldName(), enumConstantDocs);
    json.add(methodsFieldName(), methodDocs);
    return Optional.of(json);
  }

  private static JsonArray getJavadocsAsJson(List<Element> elements,
                                             Function<Element, Optional<JsonObject>> createDoc) {
    return elements.stream()
        .map(createDoc)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(JSON_ARRAY_COLLECTOR);
  }

  private Optional<JsonObject> getFieldJavadocAsJson(Element field) {
    String javadoc = processingEnv.getElementUtils().getDocComment(field);
    if (isBlank(javadoc)) {
      return Optional.empty();
    }

    JsonObject jsonDoc = new JsonObject();
    jsonDoc.add(elementNameFieldName(), field.getSimpleName().toString());
    jsonDoc.add(elementDocFieldName(), javadoc);
    return Optional.of(jsonDoc);
  }

  private Optional<JsonObject> getMethodJavadocAsJson(Element method) {
    assert method instanceof ExecutableElement;

    String methodJavadoc = processingEnv.getElementUtils().getDocComment(method);
    if (isBlank(methodJavadoc)) {
      return Optional.empty();
    }

    JsonObject jsonDoc = new JsonObject();
    jsonDoc.add(elementNameFieldName(), method.getSimpleName().toString());
    jsonDoc.add(paramTypesFieldName(), getParamErasures((ExecutableElement) method));
    jsonDoc.add(elementDocFieldName(), methodJavadoc);
    return Optional.of(jsonDoc);
  }

  private JsonArray getParamErasures(ExecutableElement executableElement) {
    Types typeUtils = processingEnv.getTypeUtils();
    return executableElement.getParameters()
        .stream()
        .map(Element::asType)
        .map(typeUtils::erasure)
        .map(TypeMirror::toString)
        .map(Json::value)
        .collect(JSON_ARRAY_COLLECTOR);
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}
