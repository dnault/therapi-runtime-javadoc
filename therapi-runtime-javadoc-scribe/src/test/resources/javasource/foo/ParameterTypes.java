package javasource.foo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * Methods whose parameter types exercise every shape of erased type name.
 * Javadoc is found at runtime only if the recorded parameter types match {@link Class#getCanonicalName()}.
 */
public class ParameterTypes<T extends Number> {

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.CLASS)
    @interface Ann {
    }

    public static class Nested {
        public class Inner {
        }
    }

    public class InnerOfGeneric {
    }

    /**
     * Constructor with a two-dimensional array of an inner class.
     */
    public ParameterTypes(Nested.Inner[][] grid, T t) {
    }

    /**
     * Every primitive type.
     */
    public void primitives(int a, boolean b, char c, long d, float e, double f, byte g, short h) {
    }

    /**
     * Arrays of primitives, classes, nested classes and generics.
     */
    public void arrays(int[] a, String[][] b, Nested.Inner[] c, List<String>[] d, long[][][] e) {
    }

    /**
     * Class type variable and generic types.
     */
    public void generics(T t, List<? extends T> l, Map<String, List<T>> m) {
    }

    /**
     * Method type variable with an intersection bound.
     */
    public <E extends Comparable<E> & java.io.Serializable> void bounded(E e, E[] es) {
    }

    /**
     * Type annotations in every position.
     */
    public void annotated(@Ann String s, java.util.@Ann List<@Ann String> l, @Ann int i,
                          String @Ann [] arr, @Ann String[] arr2, Nested.@Ann Inner n, @Ann T t) {
    }

    /**
     * Varargs.
     */
    public void varargs(String... rest) {
    }

    /**
     * Nested and inner classes.
     */
    public void nested(Nested n, Nested.Inner i, Map.Entry<String, String> e, ParameterTypes<T>.InnerOfGeneric x) {
    }

    /**
     * Overload taking an object.
     */
    public void overloaded(Object o) {
    }

    /**
     * Overload taking an object array.
     */
    public void overloaded(Object[] o) {
    }

    /**
     * Overload taking an int array.
     */
    public void overloaded(int[] o) {
    }
}
