package com.github.therapi.runtimejavadoc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;
import static java.util.regex.Pattern.compile;

public class MethodJavadoc extends BaseJavadoc {
    
    private static final Pattern LEGACY_PARAMETER_TYPE_PATTERN = compile("(?:.*:: )?([\\w.]+)\\)?");
    
    private final List<String> paramTypes;
    private final List<ParamJavadoc> params;
    private final List<ThrowsJavadoc> exceptions;
    private final Comment returns;

    public MethodJavadoc(String name,
                         List<String> paramTypes,
                         Comment comment,
                         List<ParamJavadoc> params,
                         List<ThrowsJavadoc> exceptions,
                         List<OtherJavadoc> other,
                         Comment returns,
                         List<SeeAlsoJavadoc> seeAlso) {
        super(name, comment, seeAlso, other);
        this.paramTypes = unmodifiableDefensiveCopy(paramTypes);
        this.params = unmodifiableDefensiveCopy(params);
        this.exceptions = unmodifiableDefensiveCopy(exceptions);
        this.returns = Comment.nullToEmpty(returns);
    }

    public static MethodJavadoc createEmpty(Method method) {
        return new MethodJavadoc(method.getName(), null, null, null, null, null, null, null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    public static MethodJavadoc createEmpty(Constructor<?> method) {
        return new MethodJavadoc(method.getName(), null, null, null, null, null, null, null) {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }

    public boolean isConstructor() {
        return "<init>".equals(getName());
    }

    public boolean matches(Method method) {
        return method.getName().equals(getName())
                && paramsMatch(method.getParameterTypes());
    }

    public boolean matches(Constructor<?> method) {
        return isConstructor()
                && paramsMatch(method.getParameterTypes());
    }

    private boolean paramsMatch(Class<?>[] paramTypes) {
        List<String> normalizedParamtypes = new ArrayList<>();
        for (String paramType : this.paramTypes) {
            String legacyParamType = LEGACY_PARAMETER_TYPE_PATTERN.matcher(paramType).replaceFirst("$1");
            normalizedParamtypes.add(legacyParamType);
        }
        return getCanonicalNames(paramTypes).equals(normalizedParamtypes);
    }

    private static List<String> getCanonicalNames(Class<?>[] paramTypes) {
        List<String> methodParamsTypes = new ArrayList<>();
        for (Class<?> aClass : paramTypes) {
            methodParamsTypes.add(aClass.getCanonicalName());
        }
        return methodParamsTypes;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public List<ParamJavadoc> getParams() {
        return params;
    }

    public List<ThrowsJavadoc> getThrows() {
        return exceptions;
    }

    public Comment getReturns() {
        return returns;
    }

    @Override
    public String toString() {
        return "MethodJavadoc{" +
                "name='" + getName() + '\'' +
                ", paramTypes='" + paramTypes + '\'' +
                ", comment=" + getComment() +
                ", params=" + params +
                ", exceptions=" + exceptions +
                ", other=" + getOther() +
                ", returns=" + returns +
                ", seeAlso=" + getSeeAlso() +
                '}';
    }
}
