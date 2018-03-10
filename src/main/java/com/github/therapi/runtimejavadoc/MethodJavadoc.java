package com.github.therapi.runtimejavadoc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class MethodJavadoc {
    private final String name;
    private final List<String> paramTypes;
    private final Comment comment;
    private final List<ParamJavadoc> params;
    private final List<ThrowsJavadoc> exceptions;
    private final List<OtherJavadoc> other;
    private final Comment returns;
    private final List<SeeAlsoJavadoc> seeAlso;

    public MethodJavadoc(String name,
                         List<String> paramTypes,
                         Comment comment,
                         List<ParamJavadoc> params,
                         List<ThrowsJavadoc> exceptions,
                         List<OtherJavadoc> other,
                         Comment returns,
                         List<SeeAlsoJavadoc> seeAlso) {
        this.name = name;
        this.paramTypes = paramTypes;
        this.comment = comment;
        this.params = unmodifiableDefensiveCopy(params);
        this.exceptions = unmodifiableDefensiveCopy(exceptions);
        this.other = unmodifiableDefensiveCopy(other);
        this.returns = returns;
        this.seeAlso = unmodifiableDefensiveCopy(seeAlso);
    }

    public boolean matches(Method method) {
        if (!method.getName().equals(name)) {
            return false;
        }
        List<String> methodParamsTypes = Arrays.stream(method.getParameterTypes())
                                               .map(Class::getCanonicalName)
                                               .collect(Collectors.toList());
        return methodParamsTypes.equals(paramTypes);
    }

    public String getName() {
        return name;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public Comment getComment() {
        return comment;
    }

    public List<ParamJavadoc> getParams() {
        return params;
    }

    public List<ThrowsJavadoc> getThrows() {
        return exceptions;
    }

    public List<OtherJavadoc> getOther() {
        return other;
    }

    public Comment getReturns() {
        return returns;
    }

    public List<SeeAlsoJavadoc> getSeeAlso() {
        return seeAlso;
    }

    @Override
    public String toString() {
        return "MethodJavadoc{" +
                "name='" + name + '\'' +
                ", paramTypes='" + paramTypes + '\'' +
                ", comment=" + comment +
                ", params=" + params +
                ", exceptions=" + exceptions +
                ", other=" + other +
                ", returns=" + returns +
                ", seeAlso=" + seeAlso +
                '}';
    }
}
