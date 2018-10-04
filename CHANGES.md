# Change history

## 0.8.0 - October 3, 2018

Thanks to Benny Bottema, method signatures in link tags are now parsed correctly.

### Deprecations

The `isPresent()` method on Javadoc objects is deprecated in favor of
`!isEmpty()`.


## 0.7.0 - September 1, 2018

Thanks to Benny Bottema, now compatible with Java 7.

### BREAKING API CHANGE
Public API methods that previously returned `Optional<X>` now return `X`,
always non-null.

If the Javadoc is not available, the returned object will be a placeholder
following the **Null Object Pattern**. A placeholder acts just a like a "real"
`Class/Method/FieldDoc` object, but its methods return values that model
absent Javadoc.

If you need to distinguish a placeholder from a "real"
documentation object, you can call the `isPresent()` method which always
returns `false` for placeholders.
  
Issues resolved:

* [ENHANCEMENT] #22 Make this available in Java 7 (Benny Bottema)


## 0.6.0 - May 3, 2018

Thanks to Joffrey Bion, inline tags and links in comments are now parsed
and presented as `InlineTag` and `InlineLink` comment elements. 

Issues resolved:

* [ENHANCEMENT] #17 Add links and tags elements to Comment (joffrey-bion)


## 0.5.0 - April 28, 2018

Thanks to contributions from Joffrey Bion and Simon Kissane,
it's now possible to read the Javadoc of fields and enum constants.

Issues resolved:

* [ENHANCEMENT] #15 Add field and enum constants support (joffrey-bion, skissane)
* [BUGFIX] #12 NullPointerException in RuntimeJavadoc due to null ClassLoader


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
