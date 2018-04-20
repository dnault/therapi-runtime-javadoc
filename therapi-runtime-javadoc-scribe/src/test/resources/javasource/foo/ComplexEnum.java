package javasource.foo;

/**
 * Hi, I'm a complex enum with fields.
 */
public enum ComplexEnum {

    /**
     * This is the FOO11 value documentation
     */
    FOO11("test1"),
    /**
     * This is the BAR22 value documentation
     */
    BAR22("test2"),
    /**
     * This is the BAZ33 value documentation
     */
    BAZ33("test3");

    /**
     * Content field description.
     */
    private final String content;

    private ComplexEnum(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
