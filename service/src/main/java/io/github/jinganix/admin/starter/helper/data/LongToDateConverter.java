package io.github.jinganix.admin.starter.helper.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Date;

@Converter
public class LongToDateConverter implements AttributeConverter<Long, Date> {

  @Override
  public Date convertToDatabaseColumn(Long attribute) {
    return attribute == null ? null : new Date(attribute);
  }

  @Override
  public Long convertToEntityAttribute(Date dbData) {
    return dbData == null ? null : dbData.getTime();
  }
}
