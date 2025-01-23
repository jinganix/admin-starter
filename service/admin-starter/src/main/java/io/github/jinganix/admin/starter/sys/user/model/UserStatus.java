package io.github.jinganix.admin.starter.sys.user.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.jinganix.admin.starter.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import io.github.jinganix.webpb.runtime.enumeration.EnumerationDeserializer;
import io.github.jinganix.webpb.runtime.enumeration.EnumerationSerializer;

@JsonDeserialize(using = EnumerationDeserializer.class)
@JsonSerialize(using = EnumerationSerializer.class)
public enum UserStatus implements Enumeration<Integer> {
  INACTIVE(0),
  ACTIVE(1);

  private static final IntegerEnumMapper<UserStatus> mapper = new IntegerEnumMapper<>(values());

  private final int value;

  UserStatus(int value) {
    this.value = value;
  }

  public static UserStatus fromValue(Integer value) {
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
