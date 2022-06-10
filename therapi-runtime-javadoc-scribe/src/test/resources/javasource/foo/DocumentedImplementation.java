package javasource.foo;

import javasource.foo.DocumentedInterface;

public class DocumentedImplementation implements DocumentedInterface<Integer> {
    /**
     * hoodwink a stranger
     */
    public boolean hoodwink(String i) {
        throw new UnsupportedOperationException();
    }

    // I have no javadoc of my own
    public boolean snaggle(String i) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param v the tea weight
     */
    public boolean fling(Integer v) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean fling(Object v) {
        throw new UnsupportedOperationException();
    }
}