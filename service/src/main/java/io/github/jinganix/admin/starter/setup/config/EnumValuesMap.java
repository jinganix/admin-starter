package io.github.jinganix.admin.starter.setup.config;

import io.github.jinganix.admin.starter.helper.utils.ReflectionUtils;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class EnumValuesMap {

  private EnumValuesMap() {}

  @Getter
  private static final Map<Class<?>, Map<Object, Enumeration<?>>> valuesMap = createValuesMap();

  @SuppressWarnings("unchecked")
  static Map<Class<?>, Map<Object, Enumeration<?>>> createValuesMap() {
    Map<Class<?>, Map<Object, Enumeration<?>>> valuesMap = new HashMap<>();
    for (Class<?> clazz : ReflectionUtils.findAllClasses("io.github.jinganix.admin.starter")) {
      if (Enumeration.class.isAssignableFrom(clazz)) {
        valuesMap.put(clazz, buildValueMap(clazz));
      }
    }
    return valuesMap;
  }

  public static Map<Object, Enumeration<?>> getValueMap(Class<?> clazz) {
    Map<Object, Enumeration<?>> map = valuesMap.get(clazz);
    if (map != null) {
      return map;
    }
    return buildValueMap(clazz);
  }

  @SuppressWarnings("unchecked")
  static Map<Object, Enumeration<?>> buildValueMap(Class<?> clazz) {
    if (!clazz.isEnum() || !Enumeration.class.isAssignableFrom(clazz)) {
      return null;
    }
    Class<? extends Enumeration<?>> enumClass = (Class<? extends Enumeration<?>>) clazz;
    Map<Object, Enumeration<?>> valueMap = new HashMap<>();
    for (Enumeration<?> value : enumClass.getEnumConstants()) {
      valueMap.put(value.getValue(), value);
      valueMap.put(String.valueOf(value.getValue()), value);
      valueMap.put(String.valueOf(value), value);
    }
    return valueMap;
  }
}
