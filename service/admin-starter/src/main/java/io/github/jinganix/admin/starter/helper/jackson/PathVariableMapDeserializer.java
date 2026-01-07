package io.github.jinganix.admin.starter.helper.jackson;

import java.util.Map;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;

public class PathVariableMapDeserializer<K, V> extends ValueDeserializer<Map<K, V>> {

  private JavaType javaType;

  public PathVariableMapDeserializer() {}

  public PathVariableMapDeserializer(JavaType javaType) {
    this.javaType = javaType;
  }

  @Override
  public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    if (property != null) {
      JavaType type = property.getType();
      return new PathVariableMapDeserializer<>(type);
    }
    return this;
  }

  @Override
  public Map<K, V> deserialize(JsonParser p, DeserializationContext ctx) {
    JavaType targetType = javaType;
    if (targetType == null) {
      throw new IllegalStateException("Target type cannot be determined");
    }

    String rawValue = p.getString();
    String json = convertToArrayJson(p, rawValue);

    ObjectReader mapper = (ObjectReader) p.objectReadContext();
    return mapper.forType(targetType).readValue(json);
  }

  private String convertToArrayJson(JsonParser p, String rawValue) throws DatabindException {
    if (rawValue.startsWith("{") && rawValue.endsWith("}")) {
      return rawValue;
    }
    StringBuilder builder = new StringBuilder("{");
    for (String s : rawValue.split(";")) {
      String[] parts = s.split(",");
      if (parts.length != 2) {
        throw DatabindException.from(p, "Bad value: " + rawValue);
      }
      for (int i = 0; i < parts.length; i++) {
        String str = parts[i];
        if (str.startsWith("\"")) {
          builder.append(str);
        } else {
          builder.append('"').append(str).append('"');
        }
        if (i == 0) {
          builder.append(":");
        }
      }
      builder.append(",");
    }
    if (builder.charAt(builder.length() - 1) == ',') {
      builder.deleteCharAt(builder.length() - 1);
    }
    builder.append("}");
    return builder.toString();
  }
}
