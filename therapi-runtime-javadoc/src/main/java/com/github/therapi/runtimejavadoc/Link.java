package com.github.therapi.runtimejavadoc;

import java.util.Objects;

public class Link {
    private final String label;
    private final String referencedClassName;
    private final String referencedMemberName;

    public Link(String label, String referencedClassName, String referencedMemberName) {
        this.label = label;
        this.referencedClassName = referencedClassName;
        this.referencedMemberName = referencedMemberName;
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
                && Objects.equals(referencedMemberName, link.referencedMemberName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, referencedClassName, referencedMemberName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (referencedClassName != null) {
            sb.append(referencedClassName);
        }

        if (referencedMemberName != null) {
            sb.append('#').append(referencedMemberName);
        }

        if (label != null) {
            sb.append(' ').append(label);
        }

        return sb.toString();
    }
}
