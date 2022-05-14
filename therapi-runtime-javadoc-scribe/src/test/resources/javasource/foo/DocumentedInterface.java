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
    public boolean hoodwink(String i);

    /**
     * Snaggle a kerfluffin
     *
     * @param i innocent
     * @return true if innocent hoodwinked
     * @throws UnsupportedOperationException if hoodwinking cannot be performed
     */
    public boolean snaggle(String i);

    /**
     * Fling the tea
     * @param v
     * @return
     */
    public boolean fling(T v);
}