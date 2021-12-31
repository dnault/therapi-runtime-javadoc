# therapi-runtime-javadoc

![Java 1.8+](https://img.shields.io/badge/java-1.8+-lightgray.svg)


## Bake Javadoc comments into your code

1. Add an annotation processor to your class path at compile time.
2. Read Javadoc comments at run time.

The annotation processor copies Javadoc from your source code
into class path resources.

The runtime library reads the class path resources, serving up your
Javadoc on demand.


## Coordinates

### Gradle

```groovy
dependencies {
    annotationProcessor 'com.github.therapi:therapi-runtime-javadoc-scribe:0.12.0'

    // Runtime library
    implementation 'com.github.therapi:therapi-runtime-javadoc:0.12.0'
}
```

### Maven

```xml
<!-- Annotation processor -->
<dependency>
    <groupId>com.github.therapi</groupId>
    <artifactId>therapi-runtime-javadoc-scribe</artifactId>
    <version>0.12.0</version>
    <scope>provided</scope>
</dependency>
    
<!-- Runtime library -->
<dependency>
    <groupId>com.github.therapi</groupId>
    <artifactId>therapi-runtime-javadoc</artifactId>
    <version>0.12.0</version>
</dependency>
```


## Usage

### Building a JAR with embedded Javadoc

Include the annotation processor JAR in your class path when compiling
the code whose Javadoc you want to read later at runtime. 

When building in an IDE you may have to explicitly enable annotation processing.
In IntelliJ this is done by going to 
`Preferences > Build, Execution, Deployment > Compiler > Annotation Processors`
and checking the box labeled "Enable annotation processing".


#### Selective Retention

If you only want to retain the Javadoc from certain packages, use the
`-A` javac argument to pass the `javadoc.packages` option to the annotation
processor. The value is a comma-delimited list of packages whose Javadoc
you want to keep. (Subpackages are included, recursively.)
    
For Gradle that might look like this:

```groovy
tasks.withType(JavaCompile) {            
  options.compilerArgs << "-Ajavadoc.packages=com.example,org.example"
}
```

If you don't specify any packages, the default behavior is to retain Javadoc
from all packages.


### Reading Javadoc comments at runtime

Add the runtime library as a dependecy of your project.

Read the Javadoc by calling `RuntimeJavadoc.getJavadoc` and passing a
class literal, a fully-qualified class name, or a `java.lang.reflect.Method`.
Because Javadoc comments may contain inline tags, you'll want to use a
`CommentFormatter` to convert comments to strings.

Here's an example that prints all available documentation for a class:

```java
import com.github.therapi.runtimejavadoc.*;
import java.io.IOException;

public class Example {
    // formatters are reusable and thread-safe
    private static final CommentFormatter formatter = new CommentFormatter();

    public static void printJavadoc(String fullyQualifiedClassName) throws IOException {
        ClassJavadoc classDoc = RuntimeJavadoc.getJavadoc(fullyQualifiedClassName);
        if (classDoc.isEmpty()) { // optionally skip absent documentation
            System.out.println("no documentation for " + fullyQualifiedClassName);
            return;
        }

        System.out.println(classDoc.getName());
        System.out.println(format(classDoc.getComment()));
        System.out.println();

        // @see tags
        for (SeeAlsoJavadoc see : classDoc.getSeeAlso()) {
            System.out.println("See also: " + see.getLink());
        }
        // miscellaneous and custom javadoc tags (@author, etc.)
        for (OtherJavadoc other : classDoc.getOther()) {
            System.out.println(other.getName() + ": " + format(other.getComment()));
        }

        System.out.println();
        System.out.println("CONSTRUCTORS");
        for (MethodJavadoc methodDoc : classDoc.getConstructors()) {
            printMethodJavadoc(methodDoc);
        }

        System.out.println();
        System.out.println("METHODS");
        for (MethodJavadoc methodDoc : classDoc.getMethods()) {
            printMethodJavadoc(methodDoc);
        }
    }

    private static void printMethodJavadoc(MethodJavadoc methodDoc) {
        System.out.println(methodDoc.getName() + methodDoc.getParamTypes());
        System.out.println(format(methodDoc.getComment()));
        
        if (!methodDoc.isConstructor()) {
            System.out.println("  returns " + format(methodDoc.getReturns()));
        }

        for (SeeAlsoJavadoc see : methodDoc.getSeeAlso()) {
            System.out.println("  See also: " + see.getLink());
        }
        for (OtherJavadoc other : methodDoc.getOther()) {
            System.out.println("  " + other.getName() + ": "
                + format(other.getComment()));
        }
        for (ParamJavadoc paramDoc : methodDoc.getParams()) {
            System.out.println("  param " + paramDoc.getName() + " "
                + format(paramDoc.getComment()));
        }
        for (ThrowsJavadoc throwsDoc : methodDoc.getThrows()) {
            System.out.println("  throws " + throwsDoc.getName() + " "
                + format(throwsDoc.getComment()));
        }
        System.out.println();
    }

    private static String format(Comment c) {
        return formatter.format(c);
    }
}
```

## Notice

This library includes the following third-party software,
repackaged to avoid potential dependency conflicts:

* [minimal-json](https://github.com/ralfstx/minimal-json)
(MIT License) Copyright (c) 2013, 2014 EclipseSource

* [JavaPoet](https://github.com/square/javapoet)
(Apache 2.0 License) Copyright (c) 2015 Square, Inc.
