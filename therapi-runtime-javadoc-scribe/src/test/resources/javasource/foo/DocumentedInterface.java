package javasource.foo;

/**
 * The {@code Javadoc} from this interface is used for testing
 *
 */
public interface DocumentedInterface<T extends Number> {
    /**
     * Hoodwink a kerfluffin
     *
     * @param i innocent
     * @return true if innocent hoodwinked
     * @throws UnsupportedOperationException if hoodwinking cannot be performed
     */
    public boolean hoodwink(String i) throws UnsupportedOperationException;

    /**
     * Snaggle a kerfluffin
     *
     * @param i innocent
     * @return true if innocent hoodwinked
     */
    public boolean snaggle(String i);

    /**
     * Fling the tea
     * @param v
     * @return true if flung
     * @exception UnsupportedOperationException if hoodwinking cannot be performed
     */
    public boolean fling(T v) throws UnsupportedOperationException;
}