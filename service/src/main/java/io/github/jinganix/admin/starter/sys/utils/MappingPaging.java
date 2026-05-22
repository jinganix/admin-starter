package io.github.jinganix.admin.starter.sys.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mappings({
  @Mapping(target = "page", source = "paging.pageable.pageNumber"),
  @Mapping(target = "size", source = "paging.pageable.pageSize"),
  @Mapping(target = "pages", source = "paging.totalPages"),
  @Mapping(target = "total", source = "paging.totalElements"),
  @Mapping(
      target = "records",
      source = "paging.content",
      defaultExpression = "java(java.util.Collections.emptyList())")
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface MappingPaging {}
