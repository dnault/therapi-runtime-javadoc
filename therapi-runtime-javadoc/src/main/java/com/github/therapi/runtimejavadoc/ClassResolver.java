package com.github.therapi.runtimejavadoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Searches a list of imports for a specified class.
 */
public abstract class ClassResolver implements Comparable<ClassResolver> {

    public static ClassResolver createClassResolverFor( String sourcePackage, Import...imports ) {
        return createClassResolverFor( sourcePackage, Arrays.asList(imports) );
    }

    public static ClassResolver createClassResolverFor( String sourcePackage, List<Import> imports ) {
        return new CompositeClassResolver( createClassResolversFor(sourcePackage,imports) );
    }


    private final int comparisonOrder;

    protected ClassResolver( int comparisonOrder ) {
        this.comparisonOrder = comparisonOrder;
    }

    /**
     * Do ones best to convert classRef into an instance of java.lang.Class.
     *
     * @return null if no match was found.
     */
    public abstract Class resolveClass( String classRef );


    public int compareTo( ClassResolver o ) {
        return Integer.compare( this.comparisonOrder, o.comparisonOrder );
    }


    private static Class fetchClass( String fqn ) {
        try {
            return Class.forName( fqn );
        } catch ( ClassNotFoundException e ) {
            return null;
        }
    }


    private static List<ClassResolver> createClassResolversFor( String sourcePackage, List<Import> imports ) {
        List<ClassResolver> classResolvers = new ArrayList<>();

        classResolvers.add( new FQNResolver() );
        classResolvers.add( new FullPackageResolver(sourcePackage) );
        classResolvers.add( new FullPackageResolver("java.lang") );

        for ( Import declaredImport : imports ) {
            ClassResolver newResolver = createClassResolverFor( declaredImport );

            if ( newResolver != null ) {
                classResolvers.add( newResolver );
            }
        }

        // ensure that the fully qualified comparators appear ahead of the 'all in package' imports
        Collections.sort(classResolvers);

        return classResolvers;
    }

    private static ClassResolver createClassResolverFor( Import declaredImport ) {
        if ( declaredImport.getStaticMember() == null ) {
            if ( "*".equals(declaredImport.getClassName()) ) {
                return new FullPackageResolver(declaredImport.getPkg());
            } else {
                Class importedClass = fetchClass( declaredImport.getFullyQualifiedClass() );

                if ( importedClass != null ) {
                    return new ExactClassResolver(declaredImport.getClassName(), importedClass);
                }
            }
        }

        return null;
    }

    public String resolveRef( String classRef ) {
        Class resolvedClass = resolveClass( classRef );

        return resolvedClass == null ? classRef : resolvedClass.getName();
    }


    /**
     * Given a list of ClassResolvers, return the result of the first one that returns a non-null
     * result.
     */
    private static class CompositeClassResolver extends ClassResolver {
        private List<ClassResolver> classResolvers;

        private CompositeClassResolver( List<ClassResolver> classResolvers ) {
            super(0);

            this.classResolvers = classResolvers;
        }

        public Class resolveClass( String classRef ) {
            for ( ClassResolver resolver : classResolvers ) {
                Class resolvedClass = resolver.resolveClass( classRef );

                if ( resolvedClass != null ) {
                    return resolvedClass;
                }
            }

            return null;
        }
    }

    /**
     * Resolves absolute class refs (eg x.y.Z).
     */
    private static class FQNResolver extends ClassResolver {
        public FQNResolver() {
            super(1);
        }

        public Class resolveClass( String ref ) {
            return fetchClass(ref);
        }
    }

    /**
     * Given 'import x.y.Z', match relative class refs that equal 'Z' and return the class
     * for x.y.Z.
     */
    private static class ExactClassResolver extends ClassResolver {
        private String targetRelativeRef;
        private Class  explicitClass;

        private ExactClassResolver( String targetRelativeRef, Class matchingClass ) {
            super(2);

            this.targetRelativeRef = targetRelativeRef;
            this.explicitClass     = matchingClass;
        }

        public Class resolveClass( String ref ) {
            if ( !Objects.equals(targetRelativeRef, ref) ) {
                return null;
            }

            return explicitClass;
        }
    }
    /**
     * Given 'import x.y.*', attempt to load any class ref as though it is within package x.y.
     */
    private static class FullPackageResolver extends ClassResolver {

        private String basePackage;

        private FullPackageResolver( String basePackage ) {
            super(3);

            this.basePackage = basePackage;
        }
        public Class resolveClass( String ref ) {
            return fetchClass(basePackage + "." + ref);
        }

    }
}
