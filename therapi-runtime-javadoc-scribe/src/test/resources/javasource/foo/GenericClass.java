package javasource.foo;

/**
 * The {@code Javadoc} from this class is used for testing generic classes and methods
 *
 * @author nobody@example.com
 * @custom.tag What does {@custom.inline this} mean?
 */
public class GenericClass<T> {

    /**
     * Generic method to do generic things
     */
    public T genericMethod(T generic) {
        return generic;
    }

    /**
     * Generic method to do other things
     */
    public <U extends Comparable<U>> T separateGeneric(U otherGeneric) {
        throw new UnsupportedOperationException();
    }

    public T blankGenericMethod() {
        throw new UnsupportedOperationException();
    }
}
