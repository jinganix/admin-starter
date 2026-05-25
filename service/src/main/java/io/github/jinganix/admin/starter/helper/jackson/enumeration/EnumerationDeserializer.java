package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import io.github.jinganix.admin.starter.setup.config.EnumValuesMap;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class EnumerationDeserializer<E extends Enumeration<?>> extends ValueDeserializer<E> {

  private final Map<Object, E> valueMap;

  public EnumerationDeserializer() {
    this.valueMap = null;
  }

  private EnumerationDeserializer(Map<Object, E> valueMap) {
    this.valueMap = valueMap;
  }

  @Override
  public E deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
    Map<Object, E> map = resolveValueMap(ctx);
    if (map == null) {
      return null;
    }
    if (p.hasToken(JsonToken.VALUE_STRING)) {
      return map.get(p.getString());
    }
    if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
      return map.get(p.getIntValue());
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private Map<Object, E> resolveValueMap(DeserializationContext ctx) {
    if (valueMap != null) {
      return valueMap;
    }
    if (ctx.getContextualType() == null) {
      return null;
    }
    Class<E> clazz = (Class<E>) ctx.getContextualType().getRawClass();
    return (Map<Object, E>) EnumValuesMap.getValueMap(clazz);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ValueDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) {
    Class<E> clazz = (Class<E>) ctx.getContextualType().getRawClass();
    return new EnumerationDeserializer<>((Map<Object, E>) EnumValuesMap.getValueMap(clazz));
  }
}
