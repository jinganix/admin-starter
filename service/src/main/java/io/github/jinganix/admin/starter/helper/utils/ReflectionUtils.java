package io.github.jinganix.admin.starter.helper.utils;

import com.google.common.reflect.ClassPath;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.ClassUtils;

public class ReflectionUtils {

  ReflectionUtils() {}

  public static Set<Class<?>> findAllClasses(String packageName) {
    ClassLoader classLoader = applicationClassLoader();
    try {
      return ClassPath.from(classLoader).getAllClasses().stream()
          .filter(clazz -> clazz.getPackageName().startsWith(packageName))
          .map(info -> loadClass(info.getName(), classLoader))
          .collect(Collectors.toSet());
    } catch (java.io.IOException e) {
      throw new IllegalStateException("Failed to read classpath", e);
    }
  }

  static Class<?> loadClass(String className, ClassLoader classLoader) {
    try {
      return ClassUtils.forName(normalizeClassName(className), classLoader);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Failed to load class: " + className, e);
    }
  }

  private static ClassLoader applicationClassLoader() {
    ClassLoader context = Thread.currentThread().getContextClassLoader();
    return context != null ? context : ReflectionUtils.class.getClassLoader();
  }

  static String normalizeClassName(String className) {
    String bootInfDotPrefix = "BOOT-INF.classes.";
    if (className.startsWith(bootInfDotPrefix)) {
      return className.substring(bootInfDotPrefix.length());
    }
    String bootInfPathPrefix = "BOOT-INF/classes/";
    if (className.startsWith(bootInfPathPrefix)) {
      return className.substring(bootInfPathPrefix.length()).replace('/', '.');
    }
    return className;
  }
}
