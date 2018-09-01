package com.github.therapi.runtimejavadoc;

import com.google.common.io.ByteStreams;
import com.google.testing.compile.Compilation;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

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

  @Override
  public URL findResource(String name) {
    JavaFileObject generatedResource = compilation.generatedFile(CLASS_OUTPUT, name).orElse(null);
    if (generatedResource == null) {
      return super.findResource(name);
    }
    try {
      URI uri = generatedResource.toUri();
      return new URL("compilation", "", -1, uri.getPath(), new URLStreamHandler() {
        @Override
        protected URLConnection openConnection(URL u) throws IOException {
          return new URLConnection(u) {
            @Override
            public void connect() throws IOException {
              connected = true;
            }

            @Override
            public InputStream getInputStream() throws IOException {
              return generatedResource.openInputStream();
            }
          };
        }
      });
    } catch (MalformedURLException e) {
      // Should never happen, since no validation is performed when stream handler is provided.
      throw new RuntimeException(e);
    }
  }
}
