package com.github.therapi.runtimejavadoc;

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
