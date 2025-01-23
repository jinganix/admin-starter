package io.github.jinganix.admin.starter.helper.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.util.List;

public class PathVariableArrayDeserializer<T> extends JsonDeserializer<List<T>>
    implements ContextualDeserializer {

  private JavaType javaType;

  public PathVariableArrayDeserializer() {}

  public PathVariableArrayDeserializer(JavaType javaType) {
    this.javaType = javaType;
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    if (property != null) {
      JavaType type = property.getType();
      return new PathVariableArrayDeserializer<>(type);
    }
    return this;
  }

  @Override
  public List<T> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
    JavaType targetType = javaType;
    if (targetType == null) {
      throw new IllegalStateException("Target type cannot be determined");
    }

    String rawValue = p.getText();
    String json = convertToArrayJson(rawValue);

    ObjectReader mapper = (ObjectReader) p.getCodec();
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
