package io.github.jinganix.admin.starter.helper.enumeration;

import io.github.jinganix.webpb.runtime.enumeration.Enumeration;

public class StringEnumMapper<T extends Enumeration<String>> extends EnumMapper<String, T> {

  public StringEnumMapper(T[] values) {
    super(values, Enumeration::getValue);
  }
}
