package javasource.foo;

import java.util.List;
import javasource.foo.DocumentedClass;


// I override methods of DocumentedClass with and without their own javadoc
public class OverridingClass extends DocumentedClass {

  /**
   * Super frobulate {@code a} by {@code b} using extended frobulation
   *
   * @see com.github.therapi.runtimejavadoc.DocumentedClass Hey, that's this class!
   * @see javasource.foo.DocumentedClass#someOtherMethod()
   * @see "Moomoo boy went straight to Moomoo land. Land of the moomoo's"
   * @see <a href="http://www.moomoo.land">Moomoo land</a>
   */
  public int frobulate(String a, int b) {
    throw new UnsupportedOperationException();
  }

  // I have no javadoc of my own
  public int frobulate(String a, List<Integer> b) {
    throw new UnsupportedOperationException();
  }

  /**
   * My very own method
   *
   */
  public void myOwnMethod() {
    throw new UnsupportedOperationException();
  }
}
