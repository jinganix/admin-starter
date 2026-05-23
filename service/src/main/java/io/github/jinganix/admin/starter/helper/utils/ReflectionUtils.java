package io.github.jinganix.admin.starter.helper.utils;

import com.google.common.reflect.ClassPath;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Strings;
import org.springframework.util.ClassUtils;

public class ReflectionUtils {

  ReflectionUtils() {}

  public static Set<Class<?>> findAllClasses(String packageName) {
    try {
      return ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
          .filter(clazz -> clazz.getPackageName().contains(packageName))
          .map(info -> loadClass(info.getName(), ClassLoader.getSystemClassLoader()))
          .collect(Collectors.toSet());
    } catch (java.io.IOException e) {
      throw new IllegalStateException("Failed to read classpath", e);
    }
  }

  static Class<?> loadClass(String className, ClassLoader classLoader) {
    try {
      String name = Strings.CS.removeStart(className, "BOOT-INF.classes.");
      return ClassUtils.forName(name, classLoader);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Failed to load class: " + className, e);
    }
  }
}
