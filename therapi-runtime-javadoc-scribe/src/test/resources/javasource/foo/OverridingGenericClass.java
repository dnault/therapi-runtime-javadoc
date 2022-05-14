package javasource.foo;

public class OverridingGenericClass extends GenericClass<String> {

    // I have no javadoc
    public String genericMethod(String generic) {
        return generic;
    }

    // Even though I may no look like it I override nothing but do partially hide a method
    public String separateGeneric(Integer otherGeneric) {
        throw new UnsupportedOperationException();
    }
}
