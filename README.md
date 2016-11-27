# therapi-runtime-javadoc

[![Build Status](https://travis-ci.org/dnault/therapi-runtime-javadoc.svg?branch=master)](https://travis-ci.org/dnault/therapi-runtime-javadoc)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
![Java 1.8+](https://img.shields.io/badge/java-1.8+-lightgray.svg)


Bakes Javadoc comments into your JAR at build time so they can be accessed at runtime.

## Usage

### Building a JAR with embedded Javadoc

Apply the Gradle plugin to the project whose Javadoc you wish to retain:

    buildscript {
        repositories {    
            jcenter()
        }

        dependencies {
            classpath "com.github.therapi:therapi-runtime-javadoc:0.1.2"
        }
    }

    apply plugin: 'com.github.therapi.runtime-javadoc'


### Reading Javadoc comments at runtime

Include the `therapi-runtime-javadoc` library in your class path. With Gradle, that looks like this:

    repositories {    
        jcenter()
    }

    dependencies {
        compile "com.github.therapi:therapi-runtime-javadoc:0.1.2"
    }

Read the Javadoc by creating a `RuntimeJavadocReader` and calling the `getDocumentation` method.
Because Javadoc comments may contain inline tags, you'll want to use a `CommentFormatter` to convert
comments to strings.

Here's an example that prints all available documentation for a class:

    import com.github.therapi.runtimejavadoc.*;
    import java.io.IOException;

    public class Example {
        // readers and formatters are reusable and thread-safe
        private static final RuntimeJavadocReader reader = new RuntimeJavadocReader();
        private static final CommentFormatter formatter = new CommentFormatter();

        public static void printJavadoc(String fullyQualifiedClassName) throws IOException {
            ClassJavadoc classDoc = reader.getDocumentation(fullyQualifiedClassName);
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