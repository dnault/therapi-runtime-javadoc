package javasource.foo;

import javasource.foo.DocumentedInterface;

public class VeryComplexImplementation extends DocumentedImplementation implements CompetingInterface<Integer> {
    // I have no javadoc of my own
    public boolean hoodwink(String i) {
        throw new UnsupportedOperationException();
    }

    public boolean fling(Integer v) {
        throw new UnsupportedOperationException();
    }
}