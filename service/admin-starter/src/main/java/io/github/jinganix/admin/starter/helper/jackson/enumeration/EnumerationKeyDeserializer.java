package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import io.github.jinganix.admin.starter.setup.config.EnumValuesMap;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import java.util.Map;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.ContextualKeyDeserializer;

public class EnumerationKeyDeserializer extends KeyDeserializer
    implements ContextualKeyDeserializer {

  private Map<Object, Enumeration<?>> valueMap;

  @Override
  public Object deserializeKey(String key, DeserializationContext ctx) {
    return this.valueMap.get(key);
  }

  @Override
  public KeyDeserializer createContextual(DeserializationContext ctx, BeanProperty property) {
    Class<?> clazz = ctx.getContextualType().getKeyType().getRawClass();
    this.valueMap = EnumValuesMap.getValueMap(clazz);
    return this;
  }
}
