package javasource.foo;

import java.util.List;

/**
 * The {@code Javadoc} from this class is used for testing
 *
 * @author nobody@example.com
 * @custom.tag What does {@custom.inline this} mean?
 */

public class DocumentedClass {

  /**
   * Frobulate {@code a} by {@code b}
   *
   * @param a blurtification factor
   * @param b oopsifizzle constant
   * @return {@code a} frobulated by {@code b}
   * @throws UnsupportedOperationException if frobulation cannot be performed
   * @see com.github.therapi.runtimejavadoc.DocumentedClass Hey, that's this class!
   * @see #someOtherMethod()
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
   */
  public int frobulate(String a, List<Integer> b) {
    throw new UnsupportedOperationException();
  }

  public void someOtherMethod() {

  }

  /**
   * I'm a nested class!
   */
  public static class Nested {
  }
}
