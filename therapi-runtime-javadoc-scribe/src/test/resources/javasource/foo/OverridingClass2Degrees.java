package javasource.foo;

import java.util.List;
import javasource.foo.OverridingClass;


// I override methods of DocumentedClass with and without their own javadoc
public class OverridingClass2Degrees extends OverridingClass {

  /**
   * I am also a simple method
   */
  public void skipMethod() {
    throw new UnsupportedOperationException();
  }

  public String skipGenericMethod(String generic) {
    return generic;
  }
}
