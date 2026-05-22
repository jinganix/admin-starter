package io.github.jinganix.admin.starter.jooq.strategy;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class JooqGeneratorStrategy extends DefaultGeneratorStrategy {

  @Override
  public String getJavaMemberName(Definition definition, Mode mode) {
    return super.getJavaMemberName(definition, mode).replaceAll("([a-zA-Z])_(\\d+)$", "$1$2");
  }

  @Override
  public String getJavaSetterName(Definition definition, Mode mode) {
    return super.getJavaSetterName(definition, mode).replaceAll("([a-zA-Z])_(\\d+)$", "$1$2");
  }

  @Override
  public String getJavaGetterName(Definition definition, Mode mode) {
    return super.getJavaGetterName(definition, mode).replaceAll("([a-zA-Z])_(\\d+)$", "$1$2");
  }

  @Override
  public String getJavaClassName(Definition definition, Mode mode) {
    if (mode == Mode.DEFAULT) {
      return super.getJavaClassName(definition, mode) + "Table";
    }
    return super.getJavaClassName(definition, mode);
  }
}
