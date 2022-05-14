package javasource.bar;

import java.util.List;
import javasource.foo.DocumentedClass;


// I override methods of DocumentedClass with and without their own javadoc
public class OverridingClassInAnotherPackage extends DocumentedClass {

  /**
   * Quick frobulate {@code a} by {@code b} using thin frobulation
   */
  public int frobulate(String a, int b) {
    throw new UnsupportedOperationException();
  }

  // I have no javadoc of my own
  public int frobulate(String a, List<Integer> b) {
    throw new UnsupportedOperationException();
  }
}
