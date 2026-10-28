# Change history

## 0.16.0 - October 27, 2026

:sparkles: [#83](https://github.com/dnault/therapi-runtime-javadoc/pull/83)
  The annotation processor is now declared as "isolating", so Gradle can
  process annotations incrementally instead of recompiling everything.

:bug: [#73](https://github.com/dnault/therapi-runtime-javadoc/issues/73)
  An empty `{@link}` tag, like in `@return {@link }`, no longer causes
  a NullPointerException.

:bug: [#75](https://github.com/dnault/therapi-runtime-javadoc/issues/75)
  Block tags aligned by a code formatter, like `@param  name  description`,
  are now parsed correctly. Previously, such `@param` tags lost their names
  (so all but one were discarded), and `@see` tags with a string or HTML link
  were ignored.

:bug: [#79](https://github.com/dnault/therapi-runtime-javadoc/issues/79)
  Formatting a comment with an empty `{@code}` or `{@literal}` tag no longer
  throws NullPointerException. This also affected `Comment.toString()`.
  Other tags without content, like `{@inheritDoc}`, are no longer rendered
  as `{@inheritDoc null}`.

:bug: `{@link}` and `{@value}` tags with whitespace before the closing brace,
  like `{@link Foo }`, are no longer silently dropped.

:fire: The annotation processor JAR no longer bundles a repackaged copy of
  JavaPoet. The runtime library now includes minimal-json as source code
  instead of a repackaged JAR. Its package name is unchanged, so the
  annotation processor and runtime library from different versions still
  work together.

### Breaking changes

:warning: [#79](https://github.com/dnault/therapi-runtime-javadoc/issues/79)
  `InlineTag.getValue()` now returns an empty string instead of null when
  the tag has no content, and the `InlineTag` constructor throws
  NullPointerException if the tag name is null.

### Build changes

:wrench: The project is now built with Maven instead of Gradle. The published
  POMs are self-contained, without a parent POM. The annotation processor's
  POM now declares its dependency on the runtime library with `compile`
  scope instead of `runtime` scope.

## 0.15.0 - July 23, 2022

:bug: [#69](https://github.com/dnault/therapi-runtime-javadoc/issues/69)
Fixed a regression in 0.14.0 where attempting to get Javadoc for a nested class would throw IllegalArgumentException.
Thank you to yanxiyue for investigating the issue.

:bug: [#70](https://github.com/dnault/therapi-runtime-javadoc/issues/70)
Fixed a problem where classes with absolutely no Javadoc would not inherit
method documentation.

## 0.14.0 - July 15, 2022

:sparkles: [#61](https://github.com/dnault/therapi-runtime-javadoc/issues/61)
Thanks to Sheikah45, methods without documentation now inherit documentation from superclass or implemented interface.

## 0.13.0 - January 1, 2022

:sparkles: [#52](https://github.com/dnault/therapi-runtime-javadoc/issues/52) 
  Added support for Java 16's record classes. 
  Use the new `ClassJavadoc.getRecordComponents()` method to get `@param` tags
  from the class documentation / canonical constructor. 

:bug: [#50](https://github.com/dnault/therapi-runtime-javadoc/issues/50)
  `RuntimeJavadoc.getJavadoc(Method)` no longer fails when parameters have
  certain annotations. Also, the annotation names no longer leak into
  `MethodJavadoc.getParameterTypes()`.

:bug: [#58](https://github.com/dnault/therapi-runtime-javadoc/issues/58)
  When constructor documentation is not found, the returned empty
  MethodJavadoc now has the correct method name of `<init>` instead of
  the fully-qualified class name.

:bug: [#59](https://github.com/dnault/therapi-runtime-javadoc/issues/59)
  `@see` tags in class documentation are now returned by
  `ClassJavadoc.getSeeAlso()` instead of `ClassJavadoc.getOther()`.

:fire: [#57](https://github.com/dnault/therapi-runtime-javadoc/issues/57)
  Removed the duplicate repackaged version of minimal-json from the 
  annotation processor JAR. Environments that complain about duplicate classes
  should be happier now.


### Breaking changes

:arrow_up: [#56](https://github.com/dnault/therapi-runtime-javadoc/issues/56) Java 1.8 is now the minimum required version.

:truck: Moved JavadocAnnotationProcessor to avoid split package between
annotation processor and runtime library. The new fully-qualified name
is `com.github.therapi.runtimejavadoc.scribe.JavadocAnnotationProcessor`.

## 0.12.0 - October 17, 2020

Thanks to Chris Kirk (kirkch) for the improvements and fixes in this version.

Issues resolved:

:sparkles: [#48](https://github.com/dnault/therapi-runtime-javadoc/issues/48) Use visitor pattern for formatting comments

:bug: [#21](https://github.com/dnault/therapi-runtime-javadoc/issues/21) @throws never parsed as ThrowsJavadoc

:bug: [#41](https://github.com/dnault/therapi-runtime-javadoc/issues/41) Constructor javadoc is missing when there is no class level java doc

:bug: [#43](https://github.com/dnault/therapi-runtime-javadoc/issues/43) @see on fields appears under other


## 0.11.0 - August 16, 2020

Issues resolved:

:sparkles: #37 Support reading constructor Javadoc

:bug: [#36](https://github.com/dnault/therapi-runtime-javadoc/issues/36) Reader throws AssertionError when parsing malformed "see" tag

:bug: [#38](https://github.com/dnault/therapi-runtime-javadoc/issues/38) Fix @code tag parsing

## 0.10.0 - July 19, 2020

It's now possible to get the Javadoc of nested classes in the default package.

Issues resolved:

:bug: [#35](https://github.com/dnault/therapi-runtime-javadoc/issues/35) Can't get Javadoc for nested class in default package

## 0.9.0 - November 22, 2018

Thanks to Benny Bottema, first class support for `@value` tags, and fixes for `@see` tags.

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

:sparkles: [#22](https://github.com/dnault/therapi-runtime-javadoc/issues/22) Make this available in Java 7 (Benny Bottema)


## 0.6.0 - May 3, 2018

Thanks to Joffrey Bion, inline tags and links in comments are now parsed
and presented as `InlineTag` and `InlineLink` comment elements. 

Issues resolved:

:sparkles: [#17](https://github.com/dnault/therapi-runtime-javadoc/issues/17) Add links and tags elements to Comment (joffrey-bion)


## 0.5.0 - April 28, 2018

Thanks to contributions from Joffrey Bion and Simon Kissane,
it's now possible to read the Javadoc of fields and enum constants.

Issues resolved:

:sparkles: [#15](https://github.com/dnault/therapi-runtime-javadoc/issues/15) Add field and enum constants support (joffrey-bion, skissane)

:bug: [#12](https://github.com/dnault/therapi-runtime-javadoc/issues/12) NullPointerException in RuntimeJavadoc due to null ClassLoader


## 0.4.0 - March 12, 2018

Javadoc is now stored as class path resources instead of Java classes,
yielding smaller JAR sizes.

Issues resolved:

:sparkles: [#10](https://github.com/dnault/therapi-runtime-javadoc/issues/10) Write Javadoc to class path resources instead of Java classes 

:sparkles: [#8](https://github.com/dnault/therapi-runtime-javadoc/issues/8) Don't generate companion class if there is no Javadoc


## 0.3.0 - March 11, 2018

Javadoc for all classes is now retained by default. The `@RetainJavadoc`
annotation is no longer required.
  
Breaking changes:

:truck: The annotation processor is now a separate artifact,
`com.github.therapi:therapi-runtime-javadoc-scribe`.

Issues resolved:

:sparkles: [#7](https://github.com/dnault/therapi-runtime-javadoc/issues/7) Separate the annotation processor from the runtime library

:sparkles: [#5](https://github.com/dnault/therapi-runtime-javadoc/issues/5) Provide RuntimeJavadoc.getJavadoc(Method) (Thanks @joffrey-bion!)

:sparkles: [#4](https://github.com/dnault/therapi-runtime-javadoc/issues/4) Allow the use of a package whitelist instead of annotating on each class


## 0.2.1 - August 26, 2017

:bug: [#3](https://github.com/dnault/therapi-runtime-javadoc/issues/3) ClassJavadoc.getName() should return the class name


## 0.2.0 - December 4, 2016

:tada: Complete overhaul to use an annotation processor instead of a Gradle plugin.
  The new approach is compatible with Maven projects, and works when code is run
  directly from an IDE.


## 0.1.2 - March 13, 2016

:bug: Fix reading Javadoc for inner classes.


## 0.1.1 - October 11, 2015

:sparkles: Adds CommentFormatter to help convert comments to strings.


## 0.1.0 - October 10, 2015

:tada: Initial release
