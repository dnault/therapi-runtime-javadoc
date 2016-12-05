# therapi-runtime-javadoc

[![Build Status](https://travis-ci.org/dnault/therapi-runtime-javadoc.svg?branch=master)](https://travis-ci.org/dnault/therapi-runtime-javadoc)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
![Java 1.8+](https://img.shields.io/badge/java-1.8+-lightgray.svg)


Bakes Javadoc comments into your code so they can be accessed at runtime.

## Coordinates

Gradle:

    repositories {    
        jcenter()
    }

    dependencies {
        compile 'com.github.therapi:therapi-runtime-javadoc:0.2.0'
    }

Maven*:

    <dependency>
        <groupId>com.github.therapi</groupId>
        <artifactId>therapi-runtime-javadoc</artifactId>
        <version>0.2.0</version>
    </dependency>

*Maven must be configured to use the JCenter repository. Setup instructions are
[here](https://bintray.com/bintray/jcenter) (click the wrench icon with the label "SET ME UP!").


## Usage


### Building a JAR with embedded Javadoc

Apply the `@RetainJavadoc` annotation to each class whose Javadoc you wish to read at runtime.
`@RetainJavadoc` also works as a meta-annotation (but not a meta-meta-annotation).

The Javadoc comments are read by an annotation processor. The processer is automatically
executed when the library is in your class path at compile time.

When building in an IDE, you may have to explicitly enable annotation processing.
In IntelliJ this is done by going to  `Preferences > Build, Execution, Deployment > Compiler > Annotation Processors`
and checking the box labeled "Enabled annotation processing".


### Reading Javadoc comments at runtime

Read the Javadoc by calling `RuntimeJavadoc.getJavadoc` and passing either a class literal or class name.
Because Javadoc comments may contain inline tags, you'll want to use a `CommentFormatter` to convert
comments to strings.

Here's an example that prints all available documentation for a class:

    import com.github.therapi.runtimejavadoc.*;
    import java.io.IOException;

    public class Example {
        // formatters are reusable and thread-safe
        private static final CommentFormatter formatter = new CommentFormatter();

        public static void printJavadoc(String fullyQualifiedClassName) throws IOException {
            ClassJavadoc classDoc = RuntimeJavadoc.getJavadoc(fullyQualifiedClassName).orElse(null);
            if (classDoc == null) {
                System.out.println("no documentation for " + fullyQualifiedClassName);
                return;
            }

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
            System.out.println("METHODS");

            for (MethodJavadoc methodDoc : classDoc.getMethods()) {
                System.out.println(methodDoc.getName() + methodDoc.getSignature());
                System.out.println(format(methodDoc.getComment()));
                System.out.println("  returns " + format(methodDoc.getReturns()));

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
        }

        private static String format(Comment c) {
            return formatter.format(c);
        }
    }

## Credits

This library includes version of
[JavaPoet](https://github.com/square/javapoet) repackaged to avoid dependency conflicts.
JavaPoet is distributed under the Apache License 2.0.