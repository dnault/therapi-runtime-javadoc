package com.github.therapi.runtimejavadoc;

import java.util.Objects;

public class InlineValue extends CommentElement {
    private final Value value;

    public InlineValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void visit( CommentVisitor visitor ) {
        visitor.inlineValue( value );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineValue that = (InlineValue) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public String toString() {
        return "value=" + value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
