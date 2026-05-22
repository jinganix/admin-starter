package io.github.jinganix.admin.starter.sys.user.model;

import io.github.jinganix.admin.starter.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.admin.starter.helper.jackson.enumeration.EnumerationDeserializer;
import io.github.jinganix.admin.starter.helper.jackson.enumeration.EnumerationSerializer;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import org.jooq.Converter;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

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

  public static class DbConverter implements Converter<Byte, UserStatus> {

    @Override
    public UserStatus from(Byte databaseObject) {
      return databaseObject == null ? null : UserStatus.fromValue(databaseObject.intValue());
    }

    @Override
    public Byte to(UserStatus userObject) {
      return userObject == null ? null : userObject.getValue().byteValue();
    }

    @Override
    public Class<Byte> fromType() {
      return Byte.class;
    }

    @Override
    public Class<UserStatus> toType() {
      return UserStatus.class;
    }
  }
}
