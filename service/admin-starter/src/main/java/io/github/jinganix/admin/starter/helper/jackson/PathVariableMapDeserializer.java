package io.github.jinganix.admin.starter.helper.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.util.Map;

public class PathVariableMapDeserializer<K, V> extends JsonDeserializer<Map<K, V>>
    implements ContextualDeserializer {

  private JavaType javaType;

  public PathVariableMapDeserializer() {}

  public PathVariableMapDeserializer(JavaType javaType) {
    this.javaType = javaType;
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    if (property != null) {
      JavaType type = property.getType();
      return new PathVariableMapDeserializer<>(type);
    }
    return this;
  }

  @Override
  public Map<K, V> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
    JavaType targetType = javaType;
    if (targetType == null) {
      throw new IllegalStateException("Target type cannot be determined");
    }

    String rawValue = p.getText();
    String json = convertToArrayJson(p, rawValue);

    ObjectReader mapper = (ObjectReader) p.getCodec();
    return mapper.forType(targetType).readValue(json);
  }

  private String convertToArrayJson(JsonParser p, String rawValue) throws JsonMappingException {
    if (rawValue.startsWith("{") && rawValue.endsWith("}")) {
      return rawValue;
    }
    StringBuilder builder = new StringBuilder("{");
    for (String s : rawValue.split(";")) {
      String[] parts = s.split(",");
      if (parts.length != 2) {
        throw new JsonMappingException(p, "Bad value: " + rawValue);
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
