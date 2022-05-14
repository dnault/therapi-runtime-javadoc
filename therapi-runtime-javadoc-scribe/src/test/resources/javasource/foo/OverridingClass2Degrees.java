package javasource.foo;

import java.util.List;
import javasource.foo.OverridingClass;


// I override methods of DocumentedClass with and without their own javadoc
public class OverridingClass2Degrees extends OverridingClass {

  public void simpleMethod() {
    throw new UnsupportedOperationException();
  }
}
