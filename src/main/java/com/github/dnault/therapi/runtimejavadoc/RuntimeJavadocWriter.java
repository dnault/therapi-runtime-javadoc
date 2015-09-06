package com.github.dnault.therapi.runtimejavadoc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
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
import java.util.List;

public class RuntimeJavadocWriter {
    private final File outputDir;
    private final ObjectMapper objectMapper = new ObjectMapper(new SmileFactory());
    private final ObjectMapper objectMapperReadable = new ObjectMapper();

    public RuntimeJavadocWriter(File outputDir) {
        this.outputDir = outputDir;
    }

    public boolean start(RootDoc root) throws IOException {
        for (ClassDoc c : root.classes()) {

            List<RuntimeFieldDoc> rtFields = new ArrayList<>();
            for (FieldDoc f : c.fields(false)) {
                rtFields.add(new RuntimeFieldDoc(f.qualifiedName(), f.commentText()));
            }

            List<RuntimeMethodDoc> rtMethods = new ArrayList<>();
            for (MethodDoc m : c.methods(false)) {
                rtMethods.add(new RuntimeMethodDoc(m.qualifiedName(), m.commentText(), m.signature(), convertTags(m.tags()), convertInlineTags(m.inlineTags())));
            }

            RuntimeClassDoc rtClassDoc = new RuntimeClassDoc(c.qualifiedName(), c.commentText(), rtFields, rtMethods);

            try (OutputStream os = new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.sml"))) {
                objectMapper.writeValue(os, rtClassDoc);
            }
            try (OutputStream os = new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.json"))) {
                objectMapperReadable.writerWithDefaultPrettyPrinter().writeValue(os, rtClassDoc);
            }
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(outputDir, c.qualifiedName() + ".javadoc.ser")))) {
                os.writeObject(rtClassDoc);
            }

            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(outputDir, c.qualifiedName() + ".javadoc.ser")))) {
                RuntimeClassDoc roundTrip = (RuntimeClassDoc) is.readObject();
                System.out.println(roundTrip);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String s = objectMapperReadable.writerWithDefaultPrettyPrinter().writeValueAsString(rtClassDoc);
            objectMapperReadable.registerModule(new GuavaModule());
            RuntimeClassDoc roundTrip = objectMapperReadable.readValue(s, RuntimeClassDoc.class);
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
