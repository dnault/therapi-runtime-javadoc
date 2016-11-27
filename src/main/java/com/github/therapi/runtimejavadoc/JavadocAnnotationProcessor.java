package com.github.therapi.runtimejavadoc;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.therapi.runtimejavadoc.internal.JavadocParser;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

//@SupportedAnnotationTypes("com.github.therapi.runtimejavadoc.RetainJavadoc")
//@SupportedAnnotationTypes("*")
public class JavadocAnnotationProcessor extends AbstractProcessor {


    PackageElement getPackageElement(Element e) {
        if (e instanceof PackageElement) {
            return (PackageElement) e;
        }
        return getPackageElement(e.getEnclosingElement());
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        final Elements elements = processingEnv.getElementUtils();

        // Only process once (in case an annotated class is in an annotated package)
        Set<Element> alreadyProcessed = new HashSet<>();

        for (Element e : roundEnvironment.getElementsAnnotatedWith(RetainJavadoc.class)) {
            try {

                if (e.getKind() == ElementKind.PACKAGE) {
                    System.out.println("package " + e.getSimpleName());
                    for (Element containedInPackage : e.getEnclosedElements()) {
                        System.out.println(containedInPackage.getKind() + " : " + containedInPackage.getSimpleName());

                        if (containedInPackage.getKind() == ElementKind.CLASS) {
                            generateJavadocForClass(elements, containedInPackage, alreadyProcessed);
                        }
                    }
                }

                if (e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE) {
                    generateJavadocForClass(elements, e, alreadyProcessed);
                }

            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        //return true;
        return true;
    }

    private void generateJavadocForClass(Elements elements, Element e, Set<Element> alreadyProcessed) throws IOException {
        if (!alreadyProcessed.add(e)) {
            System.out.println("already processed " + e.getSimpleName());
            return;
        }
        System.out.println("imports: " + ImportUtils.getImports(e, processingEnv));

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
                .returns(ClassJavadoc.class)
                .addStatement("$T<$T> methods = new $T<>()", List.class, MethodJavadoc.class, ArrayList.class)
                ;


        for (Element child : classElement.getEnclosedElements()) {
            if (child.getKind() != ElementKind.METHOD
                    //&& child.getKind() != ElementKind.CONSTRUCTOR
                    ) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) child;
            System.out.println(executableElement.getSimpleName());

            String methodJavadoc = elements.getDocComment(executableElement);

            if (methodJavadoc != null) {
                getJavadocBuilder
                        .addStatement("methods.add($T.parseMethodJavadoc($S, $S))", JavadocParser.class, executableElement.getSimpleName(), methodJavadoc);
            }
        }

        getJavadocBuilder
                .addStatement("return $T.parseClassJavadoc($S, methods)", JavadocParser.class, javadoc);

        MethodSpec getJavadoc = getJavadocBuilder.build();

        TypeSpec helloWorld = TypeSpec.classBuilder(getClassName(classElement) + "Javadoc")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(emptyPrivateConstructor())
                .addMethod(getString)
                .addMethod(getJavadoc)
                .build();


        String packageName = packageElement.getQualifiedName().toString();

        JavaFile javaFile = JavaFile.builder(packageName, helloWorld)
                .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private static String getClassName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf(".");
        String result = index == -1 ? qualifiedName : qualifiedName.substring(index + 1);
        return result;
    }

    private static String getClassName(TypeElement typeElement) {
        return getClassName(typeElement.getQualifiedName().toString());
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
        return Collections.singleton(RetainJavadoc.class.getCanonicalName());
    }
}