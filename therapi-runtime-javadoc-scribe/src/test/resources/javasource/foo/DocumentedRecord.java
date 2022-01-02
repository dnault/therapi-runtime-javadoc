package javasource.foo;

/**
 * I am a documented record.
 *
 * @param count lucky number
 * @param color favorite color
 */
public record DocumentedRecord(int count, String color) {

    /**
     * Secondary constructor, assumes favorite color is red.
     *
     * @param count yes, the count
     */
    public DocumentedRecord(int count) {
        this(count, "red");
    }

    /**
     * I am a custom method in a record
     *
     * @param frobulate yes or no
     * @return some string
     */
    public String someMethod(boolean frobulate) {
        return color + frobulate + count;
    }

    /**
     * I am nested in a record
     */
    public static class Nested {
    }
}
