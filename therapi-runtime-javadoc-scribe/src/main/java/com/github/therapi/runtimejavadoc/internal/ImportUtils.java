package com.github.therapi.runtimejavadoc.internal;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;

import static java.util.Collections.emptySet;

class ImportUtils {
  interface ImportAccessor {
    Set<String> getImports(Element element);
  }

  private ImportUtils() {
    throw new AssertionError("not instantiable");
  }

  public static Set<String> getImports(Element element, ProcessingEnvironment env) {
    try {
      return new JavacImportAccessor(env).getImports(element);
    } catch (Exception e) {
      System.err.println("Warning: tools.jar not in class path or processing environment does not support Trees API. Unqualified class names in Javadoc comments will not be resolved.");
      return emptySet();
    }
  }
}