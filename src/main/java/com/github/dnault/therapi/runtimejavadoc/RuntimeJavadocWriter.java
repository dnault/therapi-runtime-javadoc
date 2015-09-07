package com.github.dnault.therapi.runtimejavadoc;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.CommentElement;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.CommentText;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.InlineLink;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.InlineTag;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.Link;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.OtherDoc;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.ParamDoc;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.RtClassDoc;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.RtMethodDoc;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.SeeAlsoDoc;
import com.github.dnault.therapi.runtimejavadoc.ergonomic.ThrowsDoc;
import com.google.common.collect.ImmutableList;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.dnault.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.first;

public class RuntimeJavadocWriter {
    private final File outputDir;
    private final ObjectMapper objectMapper = new ObjectMapper(new SmileFactory());
    private final ObjectMapper objectMapperReadable = new ObjectMapper();



    public RuntimeJavadocWriter(File outputDir) {
        this.outputDir = outputDir;
    }

    public boolean start(RootDoc root) throws IOException {
        objectMapperReadable.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        for (ClassDoc c : root.classes()) {

//            List<RuntimeFieldDoc> rtFields = new ArrayList<>();
//            for (FieldDoc f : c.fields(false)) {
//                rtFields.add(new RuntimeFieldDoc(f.qualifiedName(), f.commentText()));
//            }

            List<RtMethodDoc> rtMethods = new ArrayList<>();
            for (MethodDoc m : c.methods(false)) {
                rtMethods.add(newRuntimeMethodDoc(m));
            }

            RtClassDoc rtClassDoc = new RtClassDoc(c.qualifiedName(), getComment(c.inlineTags()), getOther(c), getSeeAlso(c), rtMethods);

            try (OutputStream os = new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.sml"))) {
                objectMapper.writeValue(os, rtClassDoc);
            }
            try (OutputStream os = new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.json"))) {
                objectMapperReadable.writerWithDefaultPrettyPrinter().writeValue(os, rtClassDoc);
            }
//            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.ser")))) {
//                os.writeObject(rtClassDoc);
//            }
//
//            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(outputDir, c.qualifiedName() + ".javadoc.ser")))) {
//                RuntimeClassDoc roundTrip = (RuntimeClassDoc) is.readObject();
//                System.out.println(roundTrip);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }

            String s = objectMapperReadable.writerWithDefaultPrettyPrinter().writeValueAsString(rtClassDoc);
          // objectMapperReadable.registerModule(new GuavaModule());
            RtClassDoc roundTrip = objectMapperReadable.readValue(s, RtClassDoc.class);
            System.out.println(roundTrip);


/*
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
 */
        }
        return true;
    }

    private RtMethodDoc newRuntimeMethodDoc(MethodDoc m) {
        String name = m.name();
        String signature = m.signature();
        List<CommentElement> comment = getComment(m.inlineTags());

        List<ParamDoc> params = getParams(m);
        List<ThrowsDoc> exceptions = getThrows(m);
        List<SeeAlsoDoc> seeAlso = getSeeAlso(m);
        List<OtherDoc> other = getOther(m);
        Tag returnTag = first(m.tags("@return"));
        List<CommentElement> returns = returnTag == null ? Collections.<CommentElement>emptyList() : getComment(returnTag.inlineTags());
        return new RtMethodDoc(name, signature, comment, params, exceptions, other, returns, seeAlso);
    }

    private List<OtherDoc> getOther(Doc m) {
        List<OtherDoc> other = new ArrayList<>();
        for (Tag t : m.tags()) {
            if (t instanceof SeeTag || t instanceof ThrowsTag || t instanceof ParamTag ||
                    t.kind().equals("@return")) {
                continue;
            }

            other.add(new OtherDoc(t.name(), getComment(t.inlineTags())));
        }
        return other;
    }


    private List<SeeAlsoDoc> getSeeAlso(Doc doc) {
        List<SeeAlsoDoc> seeAlso = new ArrayList<>();
        for (SeeTag t : doc.seeTags()) {
            seeAlso.add(new SeeAlsoDoc(newLink(t)));
        }
        return seeAlso;
    }

    private List<ThrowsDoc> getThrows(ExecutableMemberDoc m) {
        List<ThrowsDoc> exceptions = new ArrayList<>();
        for (ThrowsTag t : m.throwsTags()) {
            exceptions.add(new ThrowsDoc(t.exceptionName(), getComment(t.inlineTags())));
        }
        return exceptions;
    }

    private List<ParamDoc> getParams(ExecutableMemberDoc doc) {
        List<ParamDoc> params = new ArrayList<>();
        for (ParamTag t : doc.paramTags()) {
            params.add(new ParamDoc(t.name(), getComment(t.inlineTags())));
        }
        return params;
    }

    private List<CommentElement> getComment(Tag[] inlineTags) {
        List<CommentElement> elements = new ArrayList<>();
        if (inlineTags == null) {
            return elements;
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
        return elements;
    }

    private Link newLink(SeeTag t) {
        return new Link(t.label(), t.referencedClassName(), t.referencedMemberName());
    }


    private ImmutableList<RuntimeTag> convertTags(Tag[] tags) {
        ImmutableList.Builder<RuntimeTag> list = ImmutableList.builder();
        for (Tag t : tags) {
            list.add(newRuntimeTag(t, newInlineTags(t)));
        }
        return list.build();
    }


    private ImmutableList<RuntimeTag> convertInlineTags(Tag[] tags) {
        ImmutableList.Builder<RuntimeTag> list = ImmutableList.builder();
        for (Tag t : tags) {
            list.add(newRuntimeTag(t, ImmutableList.<RuntimeTag>of()));
        }
        return list.build();
    }

    private RuntimeTag newRuntimeTag(Tag tag, ImmutableList<RuntimeTag> inlineTags) {
        if (tag instanceof SeeTag) {
            SeeTag t = (SeeTag) tag;
            return new RuntimeSeeTag(t.name(), t.text(), t.label(), t.referencedClassName(), t.referencedMemberName());
        }

        if (tag instanceof ParamTag) {
            ParamTag t = (ParamTag) tag;
            return new RuntimeParamTag(t.name(), t.text(), inlineTags, t.isTypeParameter(), t.parameterName(), t.parameterComment());
        }

        if (tag instanceof ThrowsTag) {
            ThrowsTag t = (ThrowsTag) tag;
            com.sun.javadoc.Type type = t.exceptionType();
            // todo if type is null, bail out?
            return new RuntimeThrowsTag(t.name(), t.text(), inlineTags, t.exceptionName(), t.exceptionComment(), type != null ? type.qualifiedTypeName() : null);
        }

        if ("Text".equals(tag.name())) {
            return new RuntimeTextTag(tag.text());
        }

        return new RuntimeTag(tag.name(), tag.text(), inlineTags);
    }

    private ImmutableList<RuntimeTag> newInlineTags(Tag tag) {
        ImmutableList.Builder<RuntimeTag> list = ImmutableList.builder();
        for (Tag t : tag.inlineTags()) {
            list.add(newRuntimeTag(t, ImmutableList.<RuntimeTag>of()));
        }
        return list.build();
    }

}
