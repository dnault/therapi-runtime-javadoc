/*
 * Copyright 2015 David Nault and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.therapi.runtimejavadoc.scribe;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.QualifiedNameable;
import java.util.HashSet;
import java.util.Set;

class PackageFilter {

    private final Set<String> rootPackages = new HashSet<>();
    private final Set<String> packages = new HashSet<>();
    private final Set<String> negatives = new HashSet<>();
    
    PackageFilter() {
        // leaves the package white-list empty, which implies it can be ignore
    }
    
    PackageFilter(String commaDelimitedPackages) {
        for (String pkg : commaDelimitedPackages.split(",")) {
            pkg = pkg.trim();
            if (!pkg.isEmpty()) {
                rootPackages.add(pkg);
            }
        }
        packages.addAll(rootPackages);
    }

    public boolean test(Element element) {
        final String elementPackage = getPackage(element);

        if (negatives.contains(elementPackage)) {
            return false;
        }

        if (packages.isEmpty() || packages.contains(elementPackage)) {
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
    
    boolean allowAllPackages() {
        return packages.isEmpty();
    }
}
