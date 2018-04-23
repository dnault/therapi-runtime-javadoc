package com.github.therapi.runtimejavadoc.internal;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.classDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.enumConstantsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.javadocResourceSuffix;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodDocFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodNameFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.methodsFieldName;
import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.paramTypesFieldName;
import static java.nio.charset.StandardCharsets.UTF_8;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.therapi.runtimejavadoc.RetainJavadoc;

public class JavadocAnnotationProcessor extends AbstractProcessor {
    private static final String PACKAGES_OPTION = "javadoc.packages";

    private static final Predicate<Element> ALL_PACKAGES = e -> true;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        final Elements elements = processingEnv.getElementUtils();
        final Map<String, String> options = processingEnv.getOptions();
        final String packagesOption = options.get(PACKAGES_OPTION);

        // Retain Javadoc for classes that match this predicate
        final Predicate<Element> packageFilter = packagesOption == null
                ? ALL_PACKAGES
                : new PackageFilter(packagesOption);

        // Make sure each element only gets processed once.
        final Set<Element> alreadyProcessed = new HashSet<>();

        // If retaining Javadoc for all packages, the @RetainJavadoc annotation is redundant.
        // Otherwise, make sure annotated classes have their Javadoc retained regardless of package.
        if (packageFilter != ALL_PACKAGES) {
            for (TypeElement annotation : annotations) {
                if (isRetainJavadocAnnotation(annotation)) {
                    for (Element e : roundEnvironment.getElementsAnnotatedWith(annotation)) {
                        generateJavadoc(elements, e, alreadyProcessed);
                    }
                }
            }
        }

        for (Element e : roundEnvironment.getRootElements()) {
            if (packageFilter.test(e)) {
                generateJavadoc(elements, e, alreadyProcessed);
            }
        }

        return false;
    }

    private static String getPackage(Element e) {
        while (e.getKind() != ElementKind.PACKAGE) {
            e = e.getEnclosingElement();
            if (e == null) {
                return "";
            }
        }
        return ((QualifiedNameable) e).getQualifiedName().toString();
    }

    private static class PackageFilter implements Predicate<Element> {
        private final Set<String> rootPackages = new HashSet<>();
        private final Set<String> packages = new HashSet<>();
        private final Set<String> negatives = new HashSet<>();

        private PackageFilter(String commaDelimitedPackages) {
            for (String pkg : commaDelimitedPackages.split(",")) {
                pkg = pkg.trim();
                if (!pkg.isEmpty()) {
                    rootPackages.add(pkg);
                }
            }
            packages.addAll(rootPackages);
        }

        @Override
        public boolean test(Element element) {
            final String elementPackage = getPackage(element);

            if (negatives.contains(elementPackage)) {
                return false;
            }

            if (packages.contains(elementPackage)) {
                return true;
            }

            for (String p : rootPackages) {
                if (elementPackage.startsWith(p + ".")) {
                    // Element's package is a subpackage of an included package.
                    packages.add(elementPackage);
                    return true;
                }
            }

            negatives.add(elementPackage);
            return false;
        }
    }

    private void generateJavadoc(Elements elements, Element e, Set<Element> alreadyProcessed) {
        ElementKind kind = e.getKind();
        if (kind == ElementKind.CLASS
                || kind == ElementKind.INTERFACE
                || kind == ElementKind.ENUM) {
            try {
                generateJavadocForClass(elements, e, alreadyProcessed);
            } catch (Exception ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Javadoc retention failed; " + ex, e);
                throw new RuntimeException("Javadoc retention failed for " + e, ex);
            }
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

        String classDoc = elements.getDocComment(classElement);

        if (isBlank(classDoc)) {
            classDoc = "";
        }

        JsonObject json = new JsonObject();
        json.add(classDocFieldName(), classDoc);
        JsonArray methods = new JsonArray();
        json.add(methodsFieldName(), methods);
        JsonArray enumConstants = new JsonArray();
        if (classElement.getKind() == ElementKind.ENUM)
            json.add(enumConstantsFieldName(), enumConstants);

        for (Element child : classElement.getEnclosedElements()) {
            switch (child.getKind()) {
                case ENUM_CONSTANT: {
                    VariableElement variableElement = (VariableElement) child;
                    String enumConstantJavaDoc = elements.getDocComment(variableElement);
                    if (isBlank(enumConstantJavaDoc)) {
                        break;
                    }

                    JsonObject enumConstant = new JsonObject();

                    String simpleName = variableElement.getSimpleName().toString();
                    enumConstant.add(enumConstantNameFieldName(), simpleName);
                    enumConstant.add(enumConstantDocFieldName(), enumConstantJavaDoc);
                    enumConstants.add(enumConstant);
                    break;
                }
                case METHOD: {
                    ExecutableElement executableElement = (ExecutableElement) child;

                    String methodJavadoc = elements.getDocComment(executableElement);

                    if (isBlank(methodJavadoc)) {
                        break;
                    }

                    JsonObject method = new JsonObject();

                    String simpleName = executableElement.getSimpleName().toString();
                    method.add(methodNameFieldName(), simpleName);

                    JsonArray paramTypes = new JsonArray();
                    getParamErasures(executableElement).forEach(paramTypes::add);

                    method.add(paramTypesFieldName(), paramTypes);
                    method.add(methodDocFieldName(), methodJavadoc);
                    methods.add(method);
                    break;
                }
            }
        }

        if (methods.isEmpty() && enumConstants.isEmpty() && isBlank(classDoc)) {
            return;
        }

        String packageName = packageElement.getQualifiedName().toString();
        String relativeName = getClassName(classElement) + javadocResourceSuffix();

        try (OutputStream os = processingEnv.getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, packageName, relativeName, e)
                .openOutputStream()) {
            os.write(json.toString().getBytes(UTF_8));
        }
    }


    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
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

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton(PACKAGES_OPTION);
    }
}
