package io.github.jinganix.admin.starter.helper.utils;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.util.ClassUtils;

@Slf4j
public class ReflectionUtils {

  public static Set<Class<?>> findAllClasses(String packageName) {
    try {
      return ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
          .filter(clazz -> clazz.getPackageName().contains(packageName))
          .map(
              x -> {
                try {
                  String name = Strings.CS.removeStart(x.getName(), "BOOT-INF.classes.");
                  return ClassUtils.forName(name, ReflectionUtils.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                  throw new RuntimeException(e);
                }
              })
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
