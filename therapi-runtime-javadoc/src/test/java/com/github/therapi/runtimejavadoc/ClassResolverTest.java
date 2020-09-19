package com.github.therapi.runtimejavadoc;

import com.github.therapi.runtimejavadoc.ClassResolver;
import com.github.therapi.runtimejavadoc.Import;
import com.github.therapi.runtimejavadoc.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class ClassResolverTest {
    @Test
    public void givenAValidFQN_callResolveClass_expectAMatch() {
        ClassResolver resolver = ClassResolver.createClassResolverFor("com.github.therapi");

        assertEquals( Value.class, resolver.resolveClass("com.github.therapi.runtimejavadoc.Value"));
        assertEquals(String.class, resolver.resolveClass("java.lang.String"));
       // assertEquals( RootCar.class, resolver.resolveClass("RootCar")); // NB illegal java - classes in the root package cannot be imported in Java
    }

    @Test
    public void givenAFQNThatIsNotFoundOnTheImportList_callResolveClass_expectNull() {
        ClassResolver resolver = ClassResolver.createClassResolverFor("com.github.therapi");

        assertNull(resolver.resolveClass("com.github.therapi.foo.Value"));
        assertNull(resolver.resolveClass("java.lang.Bar"));
    }

    @Test
    public void givenARelativeClassRefThatIsInSamePackageAsSourceClass_callResolveClass_expectAMatch() {
        ClassResolver resolver = ClassResolver.createClassResolverFor("com.github.therapi.runtimejavadoc");

        assertEquals(Value.class, resolver.resolveClass("Value"));
    }

    @Test
    public void givenARelativeClassRefThatIsInJavaLang_callResolveClass_expectAMatch() {
        ClassResolver resolver = ClassResolver.createClassResolverFor("com.github.therapi.runtimejavadoc");

        assertEquals( String.class, resolver.resolveClass("String") );
    }

    @Test
    public void givenARelativeClassRefThatIsExplicitlyImported_callResolveClass_expectAMatch() {
        ClassResolver resolver = ClassResolver.createClassResolverFor(
            "com.github.runtimejavadoc.foo",
            new Import("com.github.therapi.runtimejavadoc.resolvers.a", "Car")
        );

        assertEquals( com.github.therapi.runtimejavadoc.resolvers.a.Car.class, resolver.resolveClass("Car"));
    }

    @Test
    public void givenARelativeClassRefThatIsCoveredByAFullPackageImport_callResolveClass_expectAMatch() {
        ClassResolver resolver = ClassResolver.createClassResolverFor(
            "com.github.runtimejavadoc.foo",
            new Import("com.github.therapi.runtimejavadoc.resolvers.a", "*")
        );

        assertEquals( com.github.therapi.runtimejavadoc.resolvers.a.Car.class, resolver.resolveClass("Car"));
    }

    @Test
    public void givenTwoPackagesWithSameClassNameOneFullPackageImportAndOneExplicitClassImport_resolveClass_expectTheExplicitImportToWin() {
        ClassResolver resolver = ClassResolver.createClassResolverFor(
            "com.github.runtimejavadoc.foo",
            new Import("com.github.therapi.runtimejavadoc.resolvers.a", "*"),
            new Import("com.github.therapi.runtimejavadoc.resolvers.b", "Car")
        );

        assertEquals( com.github.therapi.runtimejavadoc.resolvers.b.Car.class, resolver.resolveClass("Car"));
    }
}
