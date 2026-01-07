package io.github.jinganix.admin.starter.helper.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter
public class JsonConverter implements AttributeConverter<Object, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Object attribute) {
    return objectMapper.writeValueAsString(attribute);
  }

  @Override
  public Object convertToEntityAttribute(String dbData) {
    return objectMapper.readValue(dbData, Object.class);
  }
}
