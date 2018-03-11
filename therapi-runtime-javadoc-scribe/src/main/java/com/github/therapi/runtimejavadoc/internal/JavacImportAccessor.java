package com.github.therapi.runtimejavadoc.internal;

import static java.util.Collections.emptySet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

import com.sun.source.tree.ImportTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * Implementation in separate class file so it fails gracefully in case
 * of missing classes.
 */
class JavacImportAccessor implements ImportUtils.ImportAccessor {
    private final Trees trees;

    JavacImportAccessor(ProcessingEnvironment env) {
        this.trees = Trees.instance(env);
    }

    @Override
    public Set<String> getImports(Element element) {
        TreePath path = trees.getPath(element);
        if (path == null) {
            // node could not be found
            return emptySet();
        }
        Set<String> imports = new HashSet<>();
        for (ImportTree importTree : path.getCompilationUnit().getImports()) {
            imports.add(importTree.getQualifiedIdentifier().toString());
        }
        return imports;
    }
}
