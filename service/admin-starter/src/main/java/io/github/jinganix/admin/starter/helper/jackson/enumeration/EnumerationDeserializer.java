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

  private Map<Object, E> valueMap;

  public EnumerationDeserializer() {}

  @Override
  public E deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
    if (p.hasToken(JsonToken.VALUE_STRING)) {
      return valueMap.get(p.getString());
    }
    if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
      return valueMap.get(p.getIntValue());
    }
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ValueDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) {
    Class<E> clazz = (Class<E>) ctx.getContextualType().getRawClass();
    this.valueMap = (Map<Object, E>) EnumValuesMap.getValueMap(clazz);
    return this;
  }
}
