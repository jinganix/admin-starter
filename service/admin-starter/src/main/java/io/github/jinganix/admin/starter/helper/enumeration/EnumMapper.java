package io.github.jinganix.admin.starter.helper.enumeration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Mapper for enum and value.
 *
 * @param <K> value
 * @param <V> enum
 */
public class EnumMapper<K, V> {

  private final Map<K, V> valueMap = new HashMap<>();

  /**
   * Constructor.
   *
   * @param values values
   * @param keyResolver key resolver
   */
  public EnumMapper(V[] values, Function<V, K> keyResolver) {
    for (V value : values) {
      valueMap.put(keyResolver.apply(value), value);
    }
  }

  /**
   * Enum from value.
   *
   * @param value value
   * @return {@link V}
   */
  public V fromValue(K value) {
    return valueMap.get(value);
  }
}
