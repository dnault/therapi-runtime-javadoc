package com.github.therapi.runtimejavadoc.internal;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.QualifiedNameable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

class PackageFilter implements Predicate<Element> {

  private final Set<String> rootPackages = new HashSet<>();
  private final Set<String> packages = new HashSet<>();
  private final Set<String> negatives = new HashSet<>();

  PackageFilter(String commaDelimitedPackages) {
    for (String pkg : commaDelimitedPackages.split(",")) {
      pkg = pkg.trim();
      if (!pkg.isEmpty()) {
        rootPackages.add(pkg);
      }
    }
    packages.addAll(rootPackages);
  }

  @Override
  public boolean test(Element element) {
    final String elementPackage = getPackage(element);

    if (negatives.contains(elementPackage)) {
      return false;
    }

    if (packages.contains(elementPackage)) {
      return true;
    }

    for (String p : rootPackages) {
      if (elementPackage.startsWith(p + ".")) {
        // Element's package is a subpackage of an included package.
        packages.add(elementPackage);
        return true;
      }
    }

    negatives.add(elementPackage);
    return false;
  }

  private static String getPackage(Element e) {
    while (e.getKind() != ElementKind.PACKAGE) {
      e = e.getEnclosingElement();
      if (e == null) {
        return "";
      }
    }
    return ((QualifiedNameable) e).getQualifiedName().toString();
  }
}
