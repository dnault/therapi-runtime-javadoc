package com.github.therapi.runtimejavadoc.internal;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;
import java.util.List;
import java.util.Objects;

public class MethodJavadocKey {
    private final String methodName;
    private final List<String> methodParamTypes;

    public MethodJavadocKey(String methodName, List<String> methodParamTypes) {
        this.methodName = methodName;
        this.methodParamTypes = unmodifiableDefensiveCopy(methodParamTypes);
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getMethodParamTypes() {
        return methodParamTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodJavadocKey that = (MethodJavadocKey) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(methodParamTypes, that.methodParamTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, methodParamTypes);
    }
}
