package io.github.jinganix.admin.starter.helper.jackson;

import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;

public class PathVariableArrayDeserializer<T> extends ValueDeserializer<List<T>> {

  private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

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

    return JSON_MAPPER.readerFor(targetType).readValue(json);
  }

  String convertToArrayJson(String rawValue) {
    if (rawValue.startsWith("[") && rawValue.endsWith("]")) {
      return rawValue;
    }
    StringBuilder builder = new StringBuilder("[");
    String[] parts = rawValue.split(",");
    for (int i = 0; i < parts.length; i++) {
      if (i > 0) {
        builder.append(",");
      }
      String str = parts[i];
      if (str.startsWith("\"")) {
        builder.append(str);
      } else {
        builder.append('"').append(str).append('"');
      }
    }
    builder.append("]");
    return builder.toString();
  }
}
