package com.github.therapi.runtimejavadoc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the annotated element should have its Javadoc made available at runtime.
 *
 * @see RuntimeJavadoc#getJavadoc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface RetainJavadoc {
}
