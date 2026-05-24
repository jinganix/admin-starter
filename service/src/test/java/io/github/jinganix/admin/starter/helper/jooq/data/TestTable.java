package io.github.jinganix.admin.starter.helper.jooq.data;

import io.github.jinganix.admin.starter.schema.DefaultSchema;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class TestTable extends TableImpl<TestRecord> {

  private static final long serialVersionUID = 1L;

  public static final TestTable TEST = new TestTable();

  @Override
  public Class<TestRecord> getRecordType() {
    return TestRecord.class;
  }

  public final TableField<TestRecord, Long> ID =
      createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

  public final TableField<TestRecord, Long> GROUP_ID =
      createField(
          DSL.name("group_id"),
          SQLDataType.BIGINT.defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.BIGINT)),
          this,
          "");

  public final TableField<TestRecord, String> DATA =
      createField(
          DSL.name("data"),
          SQLDataType.VARCHAR(2048).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)),
          this,
          "");

  private TestTable(Name alias, Table<TestRecord> aliased) {
    this(alias, aliased, (Field<?>[]) null, null);
  }

  private TestTable(Name alias, Table<TestRecord> aliased, Field<?>[] parameters, Condition where) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
  }

  public TestTable(String alias) {
    this(DSL.name(alias), TEST);
  }

  public TestTable(Name alias) {
    this(alias, TEST);
  }

  public TestTable() {
    this(DSL.name("test"), null);
  }

  @Override
  public Schema getSchema() {
    return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
  }

  @Override
  public UniqueKey<TestRecord> getPrimaryKey() {
    return Keys.TEST_PK;
  }

  @Override
  public TestTable as(String alias) {
    return new TestTable(DSL.name(alias), this);
  }

  @Override
  public TestTable as(Name alias) {
    return new TestTable(alias, this);
  }

  @Override
  public TestTable as(Table<?> alias) {
    return new TestTable(alias.getQualifiedName(), this);
  }

  @Override
  public TestTable rename(String name) {
    return new TestTable(DSL.name(name), null);
  }

  @Override
  public TestTable rename(Name name) {
    return new TestTable(name, null);
  }

  @Override
  public TestTable rename(Table<?> name) {
    return new TestTable(name.getQualifiedName(), null);
  }
}
