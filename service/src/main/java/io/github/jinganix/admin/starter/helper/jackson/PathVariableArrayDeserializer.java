package io.github.jinganix.admin.starter.helper.jackson;

import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;

public class PathVariableArrayDeserializer<T> extends ValueDeserializer<List<T>> {

  private JavaType javaType;

  public PathVariableArrayDeserializer() {}

  public PathVariableArrayDeserializer(JavaType javaType) {
    this.javaType = javaType;
  }

  @Override
  public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    if (property != null) {
      JavaType type = property.getType();
      return new PathVariableArrayDeserializer<>(type);
    }
    return this;
  }

  @Override
  public List<T> deserialize(JsonParser p, DeserializationContext ctx) {
    JavaType targetType = javaType;
    if (targetType == null) {
      throw new IllegalStateException("Target type cannot be determined");
    }

    String rawValue = p.getString();
    String json = convertToArrayJson(rawValue);

    ObjectReader mapper = (ObjectReader) p.objectReadContext();
    return mapper.forType(targetType).readValue(json);
  }

  private String convertToArrayJson(String rawValue) {
    if (rawValue.startsWith("[") && rawValue.endsWith("]")) {
      return rawValue;
    }
    StringBuilder builder = new StringBuilder("[");
    for (String str : rawValue.split(",")) {
      if (str.startsWith("\"")) {
        builder.append(str);
      } else {
        builder.append('"').append(str).append("\",");
      }
    }
    if (builder.charAt(builder.length() - 1) == ',') {
      builder.deleteCharAt(builder.length() - 1);
    }
    builder.append("]");
    return builder.toString();
  }
}
