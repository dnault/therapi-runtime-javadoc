package com.github.therapi.runtimejavadoc;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.github.therapi.runtimejavadoc.internal.JsonJavadocReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static com.github.therapi.runtimejavadoc.internal.RuntimeJavadocHelper.javadocResourceSuffix;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Allows access to Javadoc elements at runtime. This will only find the Javadoc of elements that were processed by
 * the annotation processor.
 */
public class RuntimeJavadoc {

  private RuntimeJavadoc() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Gets the Javadoc of the given class.
   *
   * @param clazz the class to retrieve the Javadoc for
   * @return the Javadoc of the given class, or an empty optional if no documentation was found
   */
  public static Optional<ClassJavadoc> getJavadoc(Class clazz) {
    return getJavadoc(clazz.getName(), clazz);
  }

  /**
   * Gets the Javadoc of the given class.
   *
   * @param qualifiedClassName the fully qualified name of the class to retrieve the Javadoc for
   * @return the Javadoc of the given class, or an empty optional if no documentation was found
   */
  public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName) {
    return getJavadoc(qualifiedClassName, RuntimeJavadoc.class);
  }

  /**
   * Gets the Javadoc of the given class, using the given {@link ClassLoader} to find the Javadoc resource.
   *
   * @param qualifiedClassName the fully qualified name of the class to retrieve the Javadoc for
   * @param classLoader        the class loader to use to find the Javadoc resource file
   * @return the Javadoc of the given class, or an empty optional if no documentation was found
   */
  public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, ClassLoader classLoader) {
    final String resourceName = getResourceName(qualifiedClassName);
    try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
      return parseJavadocResource(qualifiedClassName, is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the Javadoc of the given class, using the given {@link Class} object to load the Javadoc resource.
   *
   * @param qualifiedClassName the fully qualified name of the class to retrieve the Javadoc for
   * @param loader             the class object to use to find the Javadoc resource file
   * @return the Javadoc of the given class, or an empty optional if no documentation was found
   */
  public static Optional<ClassJavadoc> getJavadoc(String qualifiedClassName, Class loader) {
    final String resourceName = getResourceName(qualifiedClassName);
    try (InputStream is = loader.getResourceAsStream("/" + resourceName)) {
      return parseJavadocResource(qualifiedClassName, is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getResourceName(String qualifiedClassName) {
    return qualifiedClassName.replace(".", "/") + javadocResourceSuffix();
  }

  private static Optional<ClassJavadoc> parseJavadocResource(String qualifiedClassName, InputStream is) throws IOException {
    if (is == null) {
      return Optional.empty();
    }

    try (InputStreamReader r = new InputStreamReader(is, UTF_8)) {
      JsonObject json = Json.parse(r).asObject();
      return JsonJavadocReader.readClassJavadoc(qualifiedClassName, json);
    }
  }

  /**
   * Gets the Javadoc of the given method.
   * <p>
   * Implementation note: this method first retrieves the Javadoc of the class, and then matches the method signature
   * with the correct documentation. If the client code's purpose is to loop through all methods doc, prefer using
   * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getMethods()} on the
   * returned class doc to retrieve method docs.
   *
   * @param method the method to get the Javadoc for
   * @return the given method's Javadoc, or an empty optional if no documentation was found
   */
  public static Optional<MethodJavadoc> getJavadoc(Method method) {
    Optional<ClassJavadoc> javadoc = getJavadoc(method.getDeclaringClass());
    return javadoc.map(ClassJavadoc::getMethods).flatMap(mDocs -> findMethodJavadoc(mDocs, method));
  }

  private static Optional<MethodJavadoc> findMethodJavadoc(List<MethodJavadoc> methodDocs, Method method) {
    return methodDocs.stream().filter(m -> m.matches(method)).findAny();
  }

  /**
   * Gets the Javadoc of the given field.
   * <p>
   * Implementation note: this method first retrieves the Javadoc of the class, and then matches the field name
   * with the correct documentation. If the client code's purpose is to loop through all fields doc, prefer using
   * {@link #getJavadoc(Class)} (or one of its overloads), and calling {@link ClassJavadoc#getFields()} on the
   * returned class doc to retrieve field docs.
   *
   * @param field the field to get the Javadoc for
   * @return the given field's Javadoc, or an empty optional if no documentation was found
   */
  public static Optional<FieldJavadoc> getJavadoc(Field field) {
    Optional<ClassJavadoc> javadoc = getJavadoc(field.getDeclaringClass());
    return javadoc.map(ClassJavadoc::getFields).flatMap(fDocs -> findFieldJavadoc(fDocs, field));
  }

  private static Optional<FieldJavadoc> findFieldJavadoc(List<FieldJavadoc> fieldDocs, Field field) {
    return fieldDocs.stream().filter(m -> m.getName().equals(field.getName())).findAny();
  }

  /**
   * Gets the Javadoc of the given enum constant.
   * <p>
   * Implementation note: this method first retrieves the Javadoc of the class, and then matches the enum constant's
   * name with the correct documentation. If the client code's purpose is to loop through all enum constants docs,
   * prefer using {@link #getJavadoc(Class)} (or one of its overloads), and calling
   * {@link ClassJavadoc#getEnumConstants()} on the returned class doc to retrieve enum constant docs.
   *
   * @param enumValue the enum constant to get the Javadoc for
   * @return the given enum constant's Javadoc, or an empty optional if no documentation was found
   */
  public static Optional<FieldJavadoc> getJavadoc(Enum<?> enumValue) {
    Optional<ClassJavadoc> javadoc = getJavadoc(enumValue.getDeclaringClass());
    return javadoc.map(ClassJavadoc::getEnumConstants).flatMap(fDocs -> findEnumValueJavadoc(fDocs, enumValue));
  }

  private static Optional<FieldJavadoc> findEnumValueJavadoc(List<FieldJavadoc> fieldDocs, Enum<?> enumValue) {
    return fieldDocs.stream().filter(m -> m.getName().equals(enumValue.name())).findAny();
  }
}
