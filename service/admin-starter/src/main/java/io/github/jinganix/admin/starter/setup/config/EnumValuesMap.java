package io.github.jinganix.admin.starter.setup.config;

import io.github.jinganix.admin.starter.helper.utils.ReflectionUtils;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class EnumValuesMap {

  @Getter
  private static final Map<Class<?>, Map<Object, Enumeration<?>>> valuesMap = createValuesMap();

  @SuppressWarnings("unchecked")
  static Map<Class<?>, Map<Object, Enumeration<?>>> createValuesMap() {
    Map<Class<?>, Map<Object, Enumeration<?>>> valuesMap = new HashMap<>();
    for (Class<?> clazz : ReflectionUtils.findAllClasses("io.github.jinganix.admin.starter")) {
      if (Enumeration.class.isAssignableFrom(clazz)) {
        Map<Object, Enumeration<?>> valueMap = new HashMap<>();
        Class<Enumeration<?>> enumClass = (Class<Enumeration<?>>) clazz;
        for (Enumeration<?> value : enumClass.getEnumConstants()) {
          valueMap.put(value.getValue(), value);
          valueMap.put(String.valueOf(value.getValue()), value);
          valueMap.put(String.valueOf(value), value);
        }
        valuesMap.put(enumClass, valueMap);
      }
    }
    return valuesMap;
  }

  public static Map<Object, Enumeration<?>> getValueMap(Class<?> clazz) {
    return valuesMap.get(clazz);
  }
}
