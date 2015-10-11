# therapi-runtime-javadoc

[![Build Status](https://travis-ci.org/dnault/therapi-runtime-javadoc.svg?branch=master)](https://travis-ci.org/dnault/therapi-runtime-javadoc)
[![Apache 2.0](https://img.shields.io/github/license/dnault/therapi-runtime-javadoc.svg)](http://www.apache.org/licenses/LICENSE-2.0)


Bakes Javadoc comments into your JAR at build time so they can be accessed at runtime.

## Usage

### Building a JAR with embedded Javadoc

Apply the Gradle plugin to the project whose Javadoc you wish to retain:

    buildscript {
        repositories {    
            maven { url "https://dl.bintray.com/dnault/maven/" }
        }

        dependencies {
            classpath 'com.github.therapi:therapi-runtime-javadoc:0.1.0'
        }
    }

    apply plugin: 'com.github.therapi.runtime-javadoc'


### Reading Javadoc comments at runtime

Include the `therapi-runtime-javadoc` library in your class path. With Gradle, that looks like this:

    repositories {    
        maven { url "https://dl.bintray.com/dnault/maven/" }
    }

    dependencies {
        compile "com.github.therapi:therapi-runtime-javadoc:0.1.0"
    }

Read the Javadoc like this:

    import com.github.therapi.runtimejavadoc.*;

    public class Example {
        // readers are reusable and thread-safe
        private static final RuntimeJavadocReader reader =
            new RuntimeJavadocReader();

        public static void main(String... args) throws Exception {
            ClassJavadoc classDoc =
                reader.getDocumentation("com.example.SomeClass");
                
            // do something with the documentation
        }
    }

