package io.github.jinganix.admin.starter.sys.role.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.jinganix.admin.starter.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import io.github.jinganix.webpb.runtime.enumeration.EnumerationDeserializer;
import io.github.jinganix.webpb.runtime.enumeration.EnumerationSerializer;

@JsonDeserialize(using = EnumerationDeserializer.class)
@JsonSerialize(using = EnumerationSerializer.class)
public enum RoleStatus implements Enumeration<Integer> {
  INACTIVE(0),
  ACTIVE(1);

  private static final IntegerEnumMapper<RoleStatus> mapper = new IntegerEnumMapper<>(values());
  private final int value;

  RoleStatus(int value) {
    this.value = value;
  }

  public static RoleStatus fromValue(Integer value) {
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
