package javasource.foo;

import java.util.List;

/**
 * The {@code Javadoc} from this class is used for testing
 *
 * @author nobody@example.com
 * @custom.tag What does {@custom.inline this} mean?
 */

public class DocumentedClass<T> {

  /**
   * I'm a useful field, maybe.
   *
   * @see #frobulate(String, int) interesting method, but nothing to do with this field
   */
  private int myField;

  /**
   * I'm a field my children can see.
   */
  protected int ourField;

  /**
   * I'm a constructor!
   */
  public DocumentedClass() {
  }

  /**
   * I'm another constructor!
   *
   * @param ignore I'm a parameter!
   */
  public DocumentedClass(String ignore) {
  }

  // I am undocumented!
  public DocumentedClass(Integer ignore) {
  }

  /**
   * Frobulate {@code a} by {@code b}
   *
   * @param a blurtification factor
   * @param b oopsifizzle constant
   * @return {@code a} frobulated by {@code b}
   * @throws UnsupportedOperationException if frobulation cannot be performed
   * @see com.github.therapi.runtimejavadoc.DocumentedClass Hey, that's this class!
   * @see #someOtherMethod()
   * @see "Moomoo boy went straight to Moomoo land. Land of the moomoo's"
   * @see <a href="http://www.moomoo.land">Moomoo land</a>
   */
  public int frobulate(String a, int b) {
    throw new UnsupportedOperationException();
  }

  /**
   * Frobulate {@code a} by multiple oopsifizzle constants
   *
   * @param a blurtification factor
   * @param b oopsifizzle constants
   * @return {@code a} frobulated by {@code b}
   * @throws UnsupportedOperationException if frobulation cannot be performed
   * @see com.github.therapi.runtimejavadoc.DocumentedClass Hey, that's this class!
   * @see #someOtherMethod()
   * @see "Moomoo boy went straight to Moomoo land. Land of the moomoo's"
   * @see <a href="http://www.moomoo.land">Moomoo land</a>
   */
  public int frobulate(String a, List<Integer> b) {
    throw new UnsupportedOperationException();
  }

  public void someOtherMethod() {

  }

  /**
   * I am a simple method
   *
   * @throws UnsupportedOperationException if cannot skip
   */
  public void skipMethod() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Foo {@link Foo#bar(String).}{@value Foo#bar(String).}
   *
   * @see Foo#bar(String).
   */
  public void malformedLinks() {
  }

  /**
   * Generic method to do generic things
   */
  public T genericMethod(T generic) {
    return generic;
  }

  /**
   * Generic method to do run things
   */
  public T skipGenericMethod(T generic) {
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

  public boolean equals(Object o) {
    return super.equals(o);
  }

  /**
   * I'm a nested class!
   */
  public static class Nested {
  }
}
