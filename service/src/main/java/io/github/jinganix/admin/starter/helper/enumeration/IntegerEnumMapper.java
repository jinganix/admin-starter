package io.github.jinganix.admin.starter.helper.enumeration;

import io.github.jinganix.webpb.runtime.enumeration.Enumeration;

public class IntegerEnumMapper<T extends Enumeration<Integer>> extends EnumMapper<Integer, T> {

  public IntegerEnumMapper(T[] values) {
    super(values, Enumeration::getValue);
  }
}
