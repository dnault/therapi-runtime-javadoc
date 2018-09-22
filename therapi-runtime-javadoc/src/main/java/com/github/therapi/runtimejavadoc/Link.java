package com.github.therapi.runtimejavadoc;

import java.util.Arrays;
import java.util.Objects;

public class Link {
    private final String label;
    private final String referencedClassName;
    private final String referencedMemberName;
    private final String[] params;
    
    public Link(String label, String referencedClassName, String referencedMemberName, String[] params) {
        this.label = label;
        this.referencedClassName = referencedClassName;
        this.referencedMemberName = referencedMemberName;
        this.params = params;
    }

    public String getLabel() {
        return label;
    }

    public String getReferencedClassName() {
        return referencedClassName;
    }

    public String getReferencedMemberName() {
        return referencedMemberName;
    }
    
    public String[] getParams() {
        return params;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(label, link.label) && Objects.equals(referencedClassName, link.referencedClassName)
                && Objects.equals(referencedMemberName, link.referencedMemberName)
                && Arrays.equals(params, link.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, referencedClassName, referencedMemberName, params);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (referencedClassName != null) {
            sb.append(referencedClassName);
        }
    
        if (referencedMemberName != null) {
            sb.append('#').append(referencedMemberName);
    
            if (params != null) {
                sb.append('(');
                for (int i = 0; i < params.length; i++) {
                    sb.append(params[i]);
                    if (i < params.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
        }

        if (label != null) {
            sb.append(' ').append(label);
        }

        return sb.toString();
    }
}
