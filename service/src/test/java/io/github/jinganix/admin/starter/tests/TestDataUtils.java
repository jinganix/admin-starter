package io.github.jinganix.admin.starter.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import org.jooq.Table;

public class TestDataUtils {

  private TestDataUtils() {}

  public static Set<Table<?>> resolveTables(Class<?> type) {
    Set<Table<?>> tables = new HashSet<>();
    for (Field field : type.getDeclaredFields()) {
      int mod = field.getModifiers();
      if (!Modifier.isStatic(mod) || !Modifier.isPublic(mod)) {
        continue;
      }
      if (Table.class.isAssignableFrom(field.getType())) {
        field.setAccessible(true);
        tables.add((Table<?>) getField(field, null));
      }
    }
    return tables;
  }

  public static Object getField(Field field, Object obj) {
    try {
      return field.get(obj);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
