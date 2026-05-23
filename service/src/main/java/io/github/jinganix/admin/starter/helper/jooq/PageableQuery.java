package io.github.jinganix.admin.starter.helper.jooq;

import com.google.common.base.CaseFormat;
import io.github.jinganix.admin.starter.schema.DefaultSchema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Row;
import org.jooq.Select;
import org.jooq.SelectConnectByStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SortField;
import org.jooq.Table;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor(staticName = "of")
public class PageableQuery<R extends Record, U> {

  private static final int DEFAULT_UNPAGED_LIMIT = 20;

  private static final Map<String, Map<String, Field<?>>> FIELDS_BY_TABLE =
      new ConcurrentHashMap<>();

  static {
    DefaultSchema.DEFAULT_SCHEMA.getTables().forEach(PageableQuery::registerTable);
  }

  private final DSLContext dsl;

  private final Pageable pageable;

  private RecordMapper<R, U> recordMapper;

  static void registerTable(Table<?> table) {
    FIELDS_BY_TABLE.put(table.getName(), fieldMap(table));
  }

  private static Map<String, Field<?>> fieldMap(Table<?> table) {
    return Arrays.stream(table.fields())
        .collect(
            Collectors.toUnmodifiableMap(
                field -> CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName()),
                field -> field));
  }

  public PageableQuery<R, U> mapper(RecordMapper<R, U> recordMapper) {
    this.recordMapper = recordMapper;
    return this;
  }

  public Page<U> fetch(SelectConnectByStep<R> query) {
    int total = dsl.fetchCount(query);
    Pageable effective = resolvePageable(total);
    if (effective.isUnpaged()) {
      query.limit(DEFAULT_UNPAGED_LIMIT);
    } else {
      applyPageable(query, effective);
    }
    List<U> content = query.fetch(recordMapper);
    return new PageImpl<>(content, effective, total);
  }

  private Pageable resolvePageable(int total) {
    if (pageable.isUnpaged()) {
      return pageable;
    }
    if (total <= pageable.getOffset()) {
      int page = total == 0 ? 0 : (total - 1) / pageable.getPageSize();
      return PageRequest.of(page, pageable.getPageSize(), pageable.getSort());
    }
    return pageable;
  }

  private void applyPageable(SelectConnectByStep<R> query, Pageable pageable) {
    if (pageable.getSort().isSorted()) {
      query.orderBy(sortFields(query, pageable.getSort()));
    }
    query.limit(pageable.getPageSize()).offset(pageable.getOffset());
  }

  private List<SortField<?>> sortFields(Select<?> query, Sort sort) {
    List<SortField<?>> sortFields = new ArrayList<>();
    for (Sort.Order order : sort) {
      Field<?> field = resolveSortField(query, order.getProperty());
      sortFields.add(order.isAscending() ? field.asc() : field.desc());
    }
    return sortFields;
  }

  private Field<?> resolveSortField(Select<?> query, String property) {
    for (SelectFieldOrAsterisk item : query.$select()) {
      Field<?> field = resolveFromSelectItem(item, property);
      if (field != null) {
        return field;
      }
    }
    for (Table<?> table : query.$from()) {
      Field<?> field = lookupTableField(table.getName(), property);
      if (field != null) {
        return field;
      }
    }
    throw new IllegalArgumentException("Cannot find field: " + property);
  }

  private Field<?> resolveFromSelectItem(SelectFieldOrAsterisk item, String property) {
    if (item instanceof Field<?> field) {
      return matchTableField(field, property);
    }
    if (item instanceof Table<?> table) {
      return lookupTableField(table.getName(), property);
    }
    if (item instanceof Row row) {
      for (Field<?> field : row.fields()) {
        Field<?> matched = matchTableField(field, property);
        if (matched != null) {
          return matched;
        }
      }
    }
    return null;
  }

  private Field<?> matchTableField(Field<?> field, String property) {
    String tableName = tableName(field);
    if (tableName == null) {
      return null;
    }
    return lookupTableField(tableName, property);
  }

  private static String tableName(Field<?> field) {
    String[] parts = field.getQualifiedName().getName();
    return parts.length > 1 ? parts[0] : null;
  }

  private static Field<?> lookupTableField(String tableName, String property) {
    Map<String, Field<?>> tableFields = FIELDS_BY_TABLE.get(tableName);
    if (tableFields == null) {
      return null;
    }
    return tableFields.get(property);
  }
}
