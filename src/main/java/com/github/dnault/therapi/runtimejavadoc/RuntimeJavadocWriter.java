package com.github.dnault.therapi.runtimejavadoc;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.first;

public class RuntimeJavadocWriter {
    private final File outputDir;
    private final ObjectMapper objectMapper = new ObjectMapper();
    //private final ObjectMapper smileObjectMapper = new ObjectMapper(new SmileFactory());

    public RuntimeJavadocWriter(File outputDir) {
        this.outputDir = outputDir;
    }

    public boolean start(RootDoc root) throws IOException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        // smileObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        for (ClassDoc c : root.classes()) {

            // todo fields?

            List<MethodJavadoc> method = new ArrayList<>();
            for (MethodDoc m : c.methods(false)) {
                method.add(newRuntimeMethodDoc(m));
            }

            ClassJavadoc rtClassDoc = new ClassJavadoc(
                    c.qualifiedName(),
                    getComment(c.inlineTags()),
                    getOther(c),
                    getSeeAlso(c),
                    method);

            /*
            try (OutputStream os = new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.sml"))) {
                smileObjectMapper.writeValue(os, rtClassDoc);
            }*/

            try (OutputStream os = new FileOutputStream(getFile(outputDir, c.containingPackage().name(), c.name(), ".javadoc.json"))) {
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(os, rtClassDoc);
            }
        }

        return true;
    }

    private static File getFile(File outputDir, String packageName, String className, String extension) throws IOException {
        File parent = outputDir;
        for (String s : packageName.split("\\.")) {
            parent = new File(parent, s);
        }
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("failed to create directory: " + parent.getAbsolutePath());
        }

        return new File(parent, className + extension);
    }

    private MethodJavadoc newRuntimeMethodDoc(MethodDoc m) {
        String name = m.name();
        String signature = m.signature();
        Comment comment = getComment(m.inlineTags());

        List<ParamJavadoc> params = getParams(m);
        List<ThrowsJavadoc> exceptions = getThrows(m);
        List<SeeAlsoJavadoc> seeAlso = getSeeAlso(m);
        List<OtherJavadoc> other = getOther(m);
        Tag returnTag = first(m.tags("@return"));
        Comment returns = returnTag == null ? null : getComment(returnTag.inlineTags());
        return new MethodJavadoc(name, signature, comment, params, exceptions, other, returns, seeAlso);
    }

    private List<OtherJavadoc> getOther(Doc m) {
        List<OtherJavadoc> other = new ArrayList<>();
        for (Tag t : m.tags()) {
            if (t instanceof SeeTag || t instanceof ThrowsTag || t instanceof ParamTag ||
                    t.kind().equals("@return")) {
                continue;
            }

            other.add(new OtherJavadoc(t.name(), getComment(t.inlineTags())));
        }
        return other;
    }

    private List<SeeAlsoJavadoc> getSeeAlso(Doc doc) {
        List<SeeAlsoJavadoc> seeAlso = new ArrayList<>();
        for (SeeTag t : doc.seeTags()) {
            seeAlso.add(new SeeAlsoJavadoc(newLink(t)));
        }
        return seeAlso;
    }

    private List<ThrowsJavadoc> getThrows(ExecutableMemberDoc m) {
        List<ThrowsJavadoc> exceptions = new ArrayList<>();
        for (ThrowsTag t : m.throwsTags()) {
            exceptions.add(new ThrowsJavadoc(t.exceptionName(), getComment(t.inlineTags())));
        }
        return exceptions;
    }

    private List<ParamJavadoc> getParams(ExecutableMemberDoc doc) {
        List<ParamJavadoc> params = new ArrayList<>();
        for (ParamTag t : doc.paramTags()) {
            params.add(new ParamJavadoc(t.name(), getComment(t.inlineTags())));
        }
        return params;
    }

    private Comment getComment(Tag[] inlineTags) {
        List<CommentElement> elements = new ArrayList<>();
        if (inlineTags == null) {
            return new Comment(elements);
        }

        for (Tag t : inlineTags) {
            if (t.kind().equals("Text")) {
                elements.add(new CommentText(t.text()));
            } else if (t instanceof SeeTag) {
                elements.add(new InlineLink(newLink((SeeTag) t)));
            } else {
                elements.add(new InlineTag(t.name(), t.text()));
            }
        }
        return new Comment(elements);
    }

    private Link newLink(SeeTag t) {
        return new Link(t.label(), t.referencedClassName(), t.referencedMemberName());
    }
}
