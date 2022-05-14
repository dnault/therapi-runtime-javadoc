package javasource.foo;

import javasource.foo.DocumentedInterface;

public class ComplexImplementation implements DocumentedInterface<Integer>, CompetingInterface<Integer> {
    // I have no javadoc of my own
    public boolean hoodwink(String i) {
        throw new UnsupportedOperationException();
    }

    // I have no javadoc of my own
    public boolean snaggle(String i) {
        throw new UnsupportedOperationException();
    }

    public boolean fling(Integer v) {
        throw new UnsupportedOperationException();
    }
}