package io.github.jinganix.admin.starter.helper.jooq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.row;

import io.github.jinganix.admin.starter.helper.jooq.data.TestEntity;
import io.github.jinganix.admin.starter.helper.jooq.data.TestRecord;
import io.github.jinganix.admin.starter.helper.jooq.data.TestRecordMapper;
import io.github.jinganix.admin.starter.helper.jooq.data.TestTable;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("PageableQuery")
class PageableQueryTest extends SpringBootIntegrationTests {

  @Autowired private DSLContext dsl;

  @Autowired private TestHelper testHelper;

  private final TestRecordMapper mapper = new TestRecordMapper();

  @BeforeEach
  void setup() {
    testHelper.clearAll();
    PageableQuery.registerTable(TestTable.TEST);
    dsl.createTableIfNotExists(TestTable.TEST)
        .columns(TestTable.TEST.fields())
        .primaryKey(TestTable.TEST.ID)
        .execute();
    if (dsl.fetchCount(TestTable.TEST) > 0) {
      dsl.truncate(TestTable.TEST).execute();
    }
    insertRows(5);
  }

  @Test
  @DisplayName("should return page with content and total count when pageable")
  void shouldReturnPageWithContentAndTotalCountWhenPageable() {
    // Given
    Pageable pageable = PageRequest.of(0, 2);

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getTotalElements()).isEqualTo(5);
    assertThat(page.getNumber()).isZero();
    assertThat(page.getSize()).isEqualTo(2);
  }

  @Test
  @DisplayName("should return rows in order when sort by id ascending")
  void shouldReturnRowsInOrderWhenSortByIdAscending() {
    // Given
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getContent()).extracting(TestEntity::getId).containsExactly(1L, 2L, 3L, 4L, 5L);
  }

  @Test
  @DisplayName("should return rows in order when sort by data descending")
  void shouldReturnRowsInOrderWhenSortByDataDescending() {
    // Given
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "data"));

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getContent())
        .extracting(TestEntity::getData)
        .containsExactly("e", "d", "c", "b", "a");
  }

  @Test
  @DisplayName("should applies default limit when unpaged")
  void shouldAppliesDefaultLimitWhenUnpaged() {
    // Given
    insertRows(20);
    Pageable pageable = Pageable.unpaged();

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getContent()).hasSize(20);
    assertThat(page.getTotalElements()).isEqualTo(25);
  }

  @Test
  @DisplayName("should adjusts to last page when offset beyond total count")
  void shouldAdjustsToLastPageWhenOffsetBeyondTotalCount() {
    // Given
    Pageable pageable = PageRequest.of(10, 2);

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getNumber()).isEqualTo(2);
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getTotalElements()).isEqualTo(5);
  }

  @Test
  @DisplayName("should return correct slice when second page without sort")
  void shouldReturnCorrectSliceWhenSecondPageWithoutSort() {
    // Given
    Pageable pageable = PageRequest.of(1, 2);

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getNumber()).isEqualTo(1);
    assertThat(page.getContent()).extracting(TestEntity::getId).containsExactly(3L, 4L);
    assertThat(page.getTotalElements()).isEqualTo(5);
  }

  @Test
  @DisplayName("should return page zero when empty table and offset beyond total")
  void shouldReturnPageZeroWhenEmptyTableAndOffsetBeyondTotal() {
    // Given
    dsl.truncate(TestTable.TEST).execute();
    Pageable pageable = PageRequest.of(3, 2);

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getNumber()).isZero();
    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalElements()).isZero();
  }

  @Test
  @DisplayName("should return rows in order when multiple sort orders")
  void shouldReturnRowsInOrderWhenMultipleSortOrders() {
    // Given
    insertRows(1);
    dsl.update(TestTable.TEST)
        .set(TestTable.TEST.DATA, "z")
        .where(TestTable.TEST.ID.eq(3L))
        .execute();
    Pageable pageable =
        PageRequest.of(
            0,
            10,
            Sort.by(Sort.Order.asc("groupId"), Sort.Order.desc("id"), Sort.Order.asc("data")));

    // When
    Page<TestEntity> page =
        PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
            .mapper(mapper::toEntity)
            .fetch(dsl.selectFrom(TestTable.TEST));

    // Then
    assertThat(page.getContent())
        .extracting(TestEntity::getId)
        .containsExactly(6L, 5L, 4L, 3L, 2L, 1L);
  }

  @Test
  @DisplayName("should resolves via from clause when partial select and sort by column from table")
  void shouldResolvesViaFromClauseWhenPartialSelectAndSortByColumnFromTable() {
    // Given
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "groupId"));

    // When
    Page<TestEntity> page =
        PageableQuery.<Record1<Long>, TestEntity>of(dsl, pageable)
            .mapper(idOnly -> new TestEntity().setId(idOnly.value1()))
            .fetch(dsl.select(TestTable.TEST.ID).from(TestTable.TEST));

    // Then
    assertThat(page.getContent()).extracting(TestEntity::getId).containsExactly(1L, 2L, 3L, 4L, 5L);
  }

  @Test
  @DisplayName("should sorts via select field list when select with explicit fields")
  void shouldSortsViaSelectFieldListWhenSelectWithExplicitFields() {
    // Given
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

    // When
    Page<TestEntity> page =
        PageableQuery.<Record3<Long, String, Long>, TestEntity>of(dsl, pageable)
            .mapper(
                record ->
                    new TestEntity()
                        .setId(record.value1())
                        .setData(record.value2())
                        .setGroupId(record.value3()))
            .fetch(
                dsl.select(TestTable.TEST.ID, TestTable.TEST.DATA, TestTable.TEST.GROUP_ID)
                    .from(TestTable.TEST));

    // Then
    assertThat(page.getContent()).extracting(TestEntity::getId).containsExactly(5L, 4L, 3L, 2L, 1L);
  }

  @Test
  @DisplayName("should sorts via row fields when select with row projection")
  void shouldSortsViaRowFieldsWhenSelectWithRowProjection() {
    // Given
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "data"));

    // When
    Page<TestEntity> page =
        PageableQuery.<Record1<Record3<Long, Long, String>>, TestEntity>of(dsl, pageable)
            .mapper(
                rowRecord -> {
                  Record3<Long, Long, String> values = rowRecord.value1();
                  return new TestEntity()
                      .setId(values.value1())
                      .setGroupId(values.value2())
                      .setData(values.value3());
                })
            .fetch(
                dsl.select(row(TestTable.TEST.ID, TestTable.TEST.GROUP_ID, TestTable.TEST.DATA))
                    .from(TestTable.TEST));

    // Then
    assertThat(page.getContent())
        .extracting(TestEntity::getData)
        .containsExactly("a", "b", "c", "d", "e");
  }

  @Test
  @DisplayName("should sorts via table fields when select with table projection")
  void shouldSortsViaTableFieldsWhenSelectWithTableProjection() {
    // Given
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

    // When
    Page<TestEntity> page =
        PageableQuery.<Record1<TestRecord>, TestEntity>of(dsl, pageable)
            .mapper(tableRecord -> mapper.toEntity(tableRecord.value1()))
            .fetch(dsl.select(TestTable.TEST).from(TestTable.TEST));

    // Then
    assertThat(page.getContent()).extracting(TestEntity::getId).containsExactly(5L, 4L, 3L, 2L, 1L);
  }

  @Test
  @DisplayName("should resolves sort via from clause when unqualified select field")
  void shouldResolvesSortViaFromClauseWhenUnqualifiedSelectField() {
    // Given
    Field<Long> bareId = field(name("id"), Long.class);
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));

    // When
    Page<TestEntity> page =
        PageableQuery.<Record1<Long>, TestEntity>of(dsl, pageable)
            .mapper(idOnly -> new TestEntity().setId(idOnly.value1()))
            .fetch(dsl.select(bareId).from(TestTable.TEST));

    // Then
    assertThat(page.getContent()).extracting(TestEntity::getId).containsExactly(1L, 2L, 3L, 4L, 5L);
  }

  @Test
  @DisplayName("should matchTableField returns null when unqualified field in select")
  void shouldMatchTableFieldReturnsNullWhenUnqualifiedFieldInSelect() {
    Field<Long> bareId = field(name("id"), Long.class);
    PageableQuery<TestRecord, TestEntity> query = PageableQuery.of(dsl, Pageable.unpaged());

    Field<?> matched = ReflectionTestUtils.invokeMethod(query, "matchTableField", bareId, "id");

    assertThat(matched).isNull();
  }

  @Test
  @DisplayName("should lookupTableField returns null when unknown table name")
  void shouldLookupTableFieldReturnsNullWhenUnknownTableName() {
    Field<?> field =
        ReflectionTestUtils.invokeMethod(
            PageableQuery.class, "lookupTableField", "unknown_table", "id");

    assertThat(field).isNull();
  }

  @Test
  @DisplayName("should falls back to from clause when row projection without matching field")
  void shouldFallsBackToFromClauseWhenRowProjectionWithoutMatchingField() {
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "groupId"));

    Page<TestEntity> page =
        PageableQuery.<Record1<Record3<Long, Long, String>>, TestEntity>of(dsl, pageable)
            .mapper(
                rowRecord -> {
                  Record3<Long, Long, String> values = rowRecord.value1();
                  return new TestEntity()
                      .setId(values.value1())
                      .setGroupId(values.value2())
                      .setData(values.value3());
                })
            .fetch(
                dsl.select(row(TestTable.TEST.ID, TestTable.TEST.GROUP_ID, TestTable.TEST.DATA))
                    .from(TestTable.TEST));

    assertThat(page.getContent()).extracting(TestEntity::getGroupId).containsOnly(1L);
  }

  @Test
  @DisplayName("should resolveFromSelectItem returns null when asterisk select item")
  void shouldResolveFromSelectItemReturnsNullWhenAsteriskSelectItem() {
    PageableQuery<TestRecord, TestEntity> query = PageableQuery.of(dsl, Pageable.unpaged());

    Field<?> matched =
        org.springframework.test.util.ReflectionTestUtils.invokeMethod(
            query, "resolveFromSelectItem", asterisk(), "id");

    assertThat(matched).isNull();
  }

  @Test
  @DisplayName("should return null from helper when row item without matching field")
  void shouldReturnNullFromHelperWhenRowItemWithoutMatchingField() {
    PageableQuery<TestRecord, TestEntity> query = PageableQuery.of(dsl, Pageable.unpaged());
    org.jooq.Row row = row(field(name("unknown"), Long.class));

    Field<?> matched =
        org.springframework.test.util.ReflectionTestUtils.invokeMethod(
            query, "resolveFromSelectItem", row, "id");

    assertThat(matched).isNull();
  }

  @Test
  @DisplayName("should throws IllegalArgumentException when unknown sort field")
  void shouldThrowsIllegalArgumentExceptionWhenUnknownSortField() {
    // Given
    Pageable pageable = PageRequest.of(0, 2, Sort.by("unknownField"));

    // When / Then
    assertThatThrownBy(
            () ->
                PageableQuery.<TestRecord, TestEntity>of(dsl, pageable)
                    .mapper(mapper::toEntity)
                    .fetch(dsl.selectFrom(TestTable.TEST)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("unknownField");
  }

  private void insertRows(int count) {
    long startId = dsl.fetchCount(TestTable.TEST) + 1;
    for (long id = startId; id < startId + count; id++) {
      TestRecord record = new TestRecord();
      record.setId(id);
      record.setGroupId(1L);
      record.setData(String.valueOf((char) ('a' + id - 1)));
      dsl.insertInto(TestTable.TEST).set(record).execute();
    }
  }
}
