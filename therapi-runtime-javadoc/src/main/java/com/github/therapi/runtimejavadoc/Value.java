package com.github.therapi.runtimejavadoc;

import java.util.Objects;

public class Value {
    private final String referencedClassName;
    private final String referencedMemberName;
    
    public Value(String referencedClassName, String referencedMemberName) {
        this.referencedClassName = referencedClassName;
        this.referencedMemberName = referencedMemberName;
    }

    public String getReferencedClassName() {
        return referencedClassName;
    }

    public String getReferencedMemberName() {
        return referencedMemberName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Value link = (Value) o;
        return Objects.equals(referencedClassName, link.referencedClassName)
                && Objects.equals(referencedMemberName, link.referencedMemberName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referencedClassName, referencedMemberName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (referencedClassName != null) {
            sb.append(referencedClassName);
        }
        if (referencedMemberName != null) {
            sb.append('#').append(referencedMemberName);
        }
        return sb.toString();
    }
}
