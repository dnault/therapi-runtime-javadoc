package com.github.therapi.runtimejavadoc;

import java.util.Objects;


public final class Import {
    /**
     * The imported package, for example com.bar.foo.  A null value means that the root package.
     */
    private String pkg;

    /**
     * The name of the imported class, for example Bar.  If all classes in the package are to be
     * imported, then className will be set to '*'. className will never be set to null.
     */
    private String className;

    /**
     * The static field or method from the class.  Will be set to null when there no static
     * members have been imported, and '*' when all static members of the class are to be imported.
     */
    private String staticMember;


    public Import( String pkg, String className ) {
        this(pkg,className,null);
    }

    public Import( String pkg, String className, String staticMember ) {
        this.pkg          = pkg;
        this.className    = className;
        this.staticMember = staticMember;
    }

    public String getPkg() {
        return pkg;
    }

    public String getClassName() {
        return className;
    }

    public String getStaticMember() {
        return staticMember;
    }

    public boolean importsAllClassesWithinPackage() {
        return "*".equals( className );
    }

    public boolean importsAllStaticMembersWithingClass() {
        return "*".equals( staticMember );
    }

    public String getFullyQualifiedClass() {
        if ( importsAllClassesWithinPackage() ) {
            return null;
        }

        return pkg == null ? className : pkg + '.' + className;
    }


    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        Import anImport = (Import) o;
        return Objects.equals( pkg, anImport.pkg ) &&
            Objects.equals( className, anImport.className ) &&
            Objects.equals( staticMember, anImport.staticMember );
    }

    public int hashCode() {
        return Objects.hash( pkg, className, staticMember );
    }
//TODO test other methods
//TODO wire up to AST parser
//TODO write to json output
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if ( pkg == null ) {
            buf.append( className );
        } else {
            buf.append( pkg );
            buf.append( '.' );
            buf.append( className );
        }

        if ( staticMember != null ) {
            buf.append( '.' );
            buf.append( staticMember );
        }

        return buf.toString();
    }
}
