package io.github.jinganix.admin.starter.sys.auth.model;

import org.jooq.Converter;

public enum AuthProvider {
  USERNAME;

  public static class DbConverter implements Converter<Byte, AuthProvider> {

    @Override
    public AuthProvider from(Byte databaseObject) {
      return databaseObject == null ? null : values()[databaseObject];
    }

    @Override
    public Byte to(AuthProvider userObject) {
      return userObject == null ? null : (byte) userObject.ordinal();
    }

    @Override
    public Class<Byte> fromType() {
      return Byte.class;
    }

    @Override
    public Class<AuthProvider> toType() {
      return AuthProvider.class;
    }
  }
}
