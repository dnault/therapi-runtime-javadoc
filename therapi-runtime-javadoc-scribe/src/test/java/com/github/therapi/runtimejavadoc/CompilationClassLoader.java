package com.github.therapi.runtimejavadoc;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import com.google.common.io.ByteStreams;
import com.google.testing.compile.Compilation;

public class CompilationClassLoader extends URLClassLoader {
    private final Compilation compilation;

    public CompilationClassLoader(Compilation compilation) {
        super(new URL[]{});
        this.compilation = compilation;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        String path = name.replace(".", "/") + ".class";
        JavaFileObject generatedClass = compilation.generatedFile(CLASS_OUTPUT, path).orElse(null);
        if (generatedClass == null) {
            return super.findClass(name);
        }

        try (InputStream is = generatedClass.openInputStream()) {
            byte[] classBytes = ByteStreams.toByteArray(is);
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
