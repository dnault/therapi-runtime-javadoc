package com.github.therapi.runtimejavadoc.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.RetainJavadoc;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;

public class JavadocAnnotationProcessor extends AbstractProcessor {

    public static String javadocClassNameSuffix() {
        return "__Javadoc";
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        final Elements elements = processingEnv.getElementUtils();

        // Only process once (in case an annotated class is in an annotated package)
        Set<Element> alreadyProcessed = new HashSet<>();

        for (TypeElement annotation : annotations) {
            if (!isRetainJavadocAnnotation(annotation)) {
                continue;
            }

            for (Element e : roundEnvironment.getElementsAnnotatedWith(annotation)) {
                try {
                    generateJavadoc(elements, e, alreadyProcessed);
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }

        return false;
    }

    private void generateJavadoc(Elements elements, Element e, Set<Element> alreadyProcessed) throws IOException {
        ElementKind kind = e.getKind();
        if (kind == ElementKind.CLASS
                || kind == ElementKind.INTERFACE
                || kind == ElementKind.ENUM) {
            generateJavadocForClass(elements, e, alreadyProcessed);
        }

        for (Element enclosed : e.getEnclosedElements()) {
            generateJavadoc(elements, enclosed, alreadyProcessed);
        }
    }

    private void generateJavadocForClass(Elements elements, Element e, Set<Element> alreadyProcessed) throws IOException {
        if (!alreadyProcessed.add(e)) {
            return;
        }
        //  System.out.println("imports: " + ImportUtils.getImports(e, processingEnv));

        TypeElement classElement = (TypeElement) e;
        PackageElement packageElement = getPackageElement(classElement);

        String javadoc = elements.getDocComment(classElement);

        if (javadoc == null) {
            javadoc = "";
        }

        JavadocParser.ParsedJavadoc parsed = JavadocParser.parse(javadoc);

        MethodSpec getString = MethodSpec.methodBuilder("getString")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addStatement("return $S", parsed.getDescription())
                .build();

        MethodSpec.Builder getJavadocBuilder = MethodSpec.methodBuilder("getJavadoc")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassJavadoc.class);
        addNewListDeclaration(getJavadocBuilder, "methods", MethodJavadoc.class);

        int methodNum = 0;
        for (Element child : classElement.getEnclosedElements()) {
            if (child.getKind() != ElementKind.METHOD
                //&& child.getKind() != ElementKind.CONSTRUCTOR
                    ) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) child;

            String methodJavadoc = elements.getDocComment(executableElement);

            if (methodJavadoc != null) {
                Name simpleName = executableElement.getSimpleName();
                String paramsVarName = generateParamsVarName(simpleName, methodNum);
                addParamsVariableCreation(getJavadocBuilder, executableElement, paramsVarName);
                getJavadocBuilder.addStatement("methods.add($T.parseMethodJavadoc($S, $L, $S))", JavadocParser.class,
                                simpleName, paramsVarName, methodJavadoc);
            }
            methodNum++;
        }

        getJavadocBuilder
                .addStatement("return $T.parseClassJavadoc($S, $S, methods)", JavadocParser.class, classElement.getQualifiedName(), javadoc);

        MethodSpec getJavadoc = getJavadocBuilder.build();

        TypeSpec javadocBuddyClass = TypeSpec.classBuilder(getClassName(classElement) + javadocClassNameSuffix())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(emptyPrivateConstructor())
                .addMethod(getString)
                .addMethod(getJavadoc)
                .build();


        String packageName = packageElement.getQualifiedName().toString();

        JavaFile javaFile = JavaFile.builder(packageName, javadocBuddyClass)
                .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private static String generateParamsVarName(Name methodName, int methodNum) {
        return methodName.toString() + "Params" + methodNum;
    }

    private void addParamsVariableCreation(Builder getJavadocBuilder, ExecutableElement executableElement,
            String paramsVarName) {
        addNewListDeclaration(getJavadocBuilder, paramsVarName, String.class);
        List<String> paramTypes = getParamErasures(executableElement);
        paramTypes.forEach(qualifiedType -> getJavadocBuilder.addStatement("$L.add($S)", paramsVarName, qualifiedType));
    }

    private static void addNewListDeclaration(Builder builder, String varName, Class<?> eltType) {
        builder.addStatement("$T<$T> $L = new $T<>()", List.class, eltType, varName, ArrayList.class);
    }

    private List<String> getParamErasures(ExecutableElement executableElement) {
        Types typeUtils = processingEnv.getTypeUtils();
        return executableElement.getParameters()
                                .stream()
                                .map(Element::asType)
                                .map(typeUtils::erasure)
                                .map(TypeMirror::toString)
                                .collect(Collectors.toList());
    }

    private static PackageElement getPackageElement(Element e) {
        if (e instanceof PackageElement) {
            return (PackageElement) e;
        }
        return getPackageElement(e.getEnclosingElement());
    }

    private static boolean isRetainJavadocAnnotation(TypeElement annotation) {
        return annotation.getQualifiedName().toString().equals(RetainJavadoc.class.getName())
                || annotation.getAnnotation(RetainJavadoc.class) != null;
    }

    private static String getClassName(TypeElement typeElement) {
        String typeName = typeElement.getQualifiedName().toString();
        String packageName = getPackageElement(typeElement).getQualifiedName().toString();

        if (!packageName.isEmpty()) {
            typeName = typeName.substring(packageName.length() + 1);
            typeName = typeName.replace(".", "$");
        }

        return typeName;
    }


    private static MethodSpec emptyPrivateConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
//        return Collections.singleton(RetainJavadoc.class.getCanonicalName());
        return Collections.singleton("*");
    }
}
