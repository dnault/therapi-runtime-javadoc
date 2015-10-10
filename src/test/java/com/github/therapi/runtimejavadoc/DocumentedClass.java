package com.github.therapi.runtimejavadoc;

/**
 * The {@code Javadoc} from this class is used for testing {@link RuntimeJavadocDoclet}.
 *
 * @author nobody@example.com
 * @custom.tag What does {@custom.inline this} mean?
 * @see RuntimeJavadocDocletTest The related unit test
 * @see RuntimeJavadocWriter
 */
public class DocumentedClass {

    /**
     * Frobulate {@code a} by {@code b}
     *
     * @param a blurtification factor
     * @param b oopsifizzle constant
     * @return {@code a} frobulated by {@code b}
     * @throws UnsupportedOperationException if frobulation cannot be performed
     * @see DocumentedClass Hey, that's this class!
     * @see #someOtherMethod()
     */
    public int frobulate(String a, int b) {
        throw new UnsupportedOperationException();
    }

    public void someOtherMethod() {

    }
}
