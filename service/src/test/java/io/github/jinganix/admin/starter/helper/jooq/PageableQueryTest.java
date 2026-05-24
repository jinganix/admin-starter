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
  @DisplayName("Given pageable -> returns page with content and total count")
  void givenPageable() {
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
  @DisplayName("Given sort by id ascending -> returns rows in order")
  void givenSortByIdAsc() {
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
  @DisplayName("Given sort by data descending -> returns rows in order")
  void givenSortByDataDesc() {
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
  @DisplayName("Given unpaged -> applies default limit")
  void givenUnpaged() {
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
  @DisplayName("Given offset beyond total count -> adjusts to last page")
  void givenOffsetBeyondCount() {
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
  @DisplayName("Given second page without sort -> returns correct slice")
  void givenSecondPageWithoutSort() {
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
  @DisplayName("Given empty table and offset beyond total -> returns page zero")
  void givenEmptyTableAndOffsetBeyondShouldReturnPageZero() {
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
  @DisplayName("Given multiple sort orders -> returns rows in order")
  void givenMultipleSortOrders() {
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
  @DisplayName("Given partial select and sort by column from table -> resolves via from clause")
  void givenPartialSelectAndSortByColumnFromTable() {
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
  @DisplayName("Given select with explicit fields -> sorts via select field list")
  void givenSelectWithExplicitFields() {
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
  @DisplayName("Given select with row projection -> sorts via row fields")
  void givenSelectWithRowProjection() {
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
  @DisplayName("Given select with table projection -> sorts via table fields")
  void givenSelectWithTableProjection() {
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
  @DisplayName("Given unqualified select field -> resolves sort via from clause")
  void givenUnqualifiedSelectField() {
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
  @DisplayName("Given unqualified field in select -> matchTableField returns null")
  void givenUnqualifiedFieldInSelectMatchTableFieldReturnsNull() {
    Field<Long> bareId = field(name("id"), Long.class);
    PageableQuery<TestRecord, TestEntity> query = PageableQuery.of(dsl, Pageable.unpaged());

    Field<?> matched = ReflectionTestUtils.invokeMethod(query, "matchTableField", bareId, "id");

    assertThat(matched).isNull();
  }

  @Test
  @DisplayName("Given unknown table name -> lookupTableField returns null")
  void givenUnknownTableNameLookupTableFieldReturnsNull() {
    Field<?> field =
        ReflectionTestUtils.invokeMethod(
            PageableQuery.class, "lookupTableField", "unknown_table", "id");

    assertThat(field).isNull();
  }

  @Test
  @DisplayName("Given row projection without matching field -> falls back to from clause")
  void givenRowProjectionWithoutMatchingField() {
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
  @DisplayName("Given asterisk select item -> resolveFromSelectItem returns null")
  void givenAsteriskSelectItemResolveFromSelectItemReturnsNull() {
    PageableQuery<TestRecord, TestEntity> query = PageableQuery.of(dsl, Pageable.unpaged());

    Field<?> matched =
        org.springframework.test.util.ReflectionTestUtils.invokeMethod(
            query, "resolveFromSelectItem", asterisk(), "id");

    assertThat(matched).isNull();
  }

  @Test
  @DisplayName("Given row item without matching field -> returns null from helper")
  void givenRowItemWithoutMatchingField() {
    PageableQuery<TestRecord, TestEntity> query = PageableQuery.of(dsl, Pageable.unpaged());
    org.jooq.Row row = row(field(name("unknown"), Long.class));

    Field<?> matched =
        org.springframework.test.util.ReflectionTestUtils.invokeMethod(
            query, "resolveFromSelectItem", row, "id");

    assertThat(matched).isNull();
  }

  @Test
  @DisplayName("Given unknown sort field -> throws IllegalArgumentException")
  void givenUnknownSortField() {
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
