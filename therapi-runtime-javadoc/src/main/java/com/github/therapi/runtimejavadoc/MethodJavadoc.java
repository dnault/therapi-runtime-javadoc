package com.github.therapi.runtimejavadoc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.unmodifiableDefensiveCopy;

public class MethodJavadoc extends BaseJavadoc {
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

    public boolean matches(Method method) {
        if (!method.getName().equals(getName())) {
            return false;
        }
        List<String> methodParamsTypes = new ArrayList<>();
        for (Class<?> aClass : method.getParameterTypes()) {
            methodParamsTypes.add(aClass.getCanonicalName());
        }
        return methodParamsTypes.equals(paramTypes);
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
