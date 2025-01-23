package io.github.jinganix.admin.starter.sys.permission.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.jinganix.admin.starter.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import io.github.jinganix.webpb.runtime.enumeration.EnumerationDeserializer;
import io.github.jinganix.webpb.runtime.enumeration.EnumerationSerializer;

@JsonDeserialize(using = EnumerationDeserializer.class)
@JsonSerialize(using = EnumerationSerializer.class)
public enum PermissionType implements Enumeration<Integer> {
  GROUP(0),
  API(1),
  UI(2);

  private static final IntegerEnumMapper<PermissionType> mapper = new IntegerEnumMapper<>(values());

  private final int value;

  PermissionType(int value) {
    this.value = value;
  }

  public static PermissionType fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get int value.
   *
   * @return value of the enum
   */
  @Override
  public Integer getValue() {
    return this.value;
  }
}
