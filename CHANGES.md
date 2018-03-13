# Change history

## 0.4.0 - March 12, 2018

Javadoc is now stored as class path resources instead of Java classes,
yielding smaller JAR sizes.

Issues resolved:

* [ENHANCEMENT] #10 Write Javadoc to class path resources instead of Java classes 
* [ENHANCEMENT] #8 Don't generate companion class if there is no Javadoc


## 0.3.0 - March 11, 2018

Javadoc for all classes is now retained by default. The `@RetainJavadoc`
annotation is no longer required.
  
Breaking changes:

* The annotation processor is now a separate artifact,
`com.github.therapi:therapi-runtime-javadoc-scribe`.

Issues resolved:

* [ENHANCEMENT] #7 Separate the annotation processor from the runtime library
* [ENHANCEMENT] #5 Provide RuntimeJavadoc.getJavadoc(Method) (Thanks @joffrey-bion!)
* [ENHANCEMENT] #4 Allow the use of a package whitelist instead of annotating on each class


## 0.2.1 - August 26, 2017

* [BUGFIX] #3 ClassJavadoc.getName() should return the class name


## 0.2.0 - December 4, 2016

* Complete overhaul to use an annotation processor instead of a Gradle plugin.
  The new approach is compatible with Maven projects, and works when code is run
  directly from an IDE.


## 0.1.2 - March 13, 2016

* [BUGFIX] Fix reading Javadoc for inner classes.


## 0.1.1 - October 11, 2015

* [FEATURE] Adds CommentFormatter to help convert comments to strings.


## 0.1.0 - October 10, 2015

* Initial release
