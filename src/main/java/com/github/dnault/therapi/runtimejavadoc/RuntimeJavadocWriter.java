package com.github.dnault.therapi.runtimejavadoc;


import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;

public class RuntimeJavadocWriter {
    private final File outputDir;

    public RuntimeJavadocWriter(File outputDir) {
        this.outputDir = outputDir;
    }

    public boolean start(RootDoc root) throws IOException {
        for (ClassDoc c : root.classes()) {
            print(c.qualifiedName(), c.commentText());
            for (FieldDoc f : c.fields(false)) {
                print(f.qualifiedName(), f.commentText());
            }
            for (MethodDoc m : c.methods(false)) {
                print(m.qualifiedName(), m.commentText());
                if (m.commentText() != null && m.commentText().length() > 0) {
                    for (ParamTag p : m.paramTags())
                        print(m.qualifiedName() + "@" + p.parameterName(), p.parameterComment());
                    for (Tag t : m.tags("return")) {
                        if (t.text() != null && t.text().length() > 0)
                            print(m.qualifiedName() + "@return", t.text());
                    }
                }
            }
        }
        return true;
    }

    private void print(String name, String comment) throws IOException {
        System.out.println(name + ": " + comment);
        //if (comment != null && comment.length() > 0) {
        //  new FileWriter(new File(outputDir, name + ".txt")).append(comment).close();
        //}
    }
}
