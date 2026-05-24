package io.github.jinganix.admin.starter.helper.jooq;

import static org.assertj.core.api.Assertions.assertThat;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ConditionBuilder")
class ConditionBuilderTest {

  private static final Field<Long> ID_FIELD = DSL.field("id", Long.class);
  private static final Field<String> NAME_FIELD = DSL.field("name", String.class);

  @Test
  @DisplayName("should should return new instance when builder call")
  void shouldShouldReturnNewInstanceWhenBuilderCall() {
    ConditionBuilder builder = ConditionBuilder.builder();

    assertThat(builder).isNotNull();
    assertThat(builder.build()).isEqualTo(DSL.noCondition());
    assertThat(builder.nullable()).isNull();
  }

  @Nested
  @DisplayName("when combining conditions with and")
  class WhenCombiningConditionsWithAnd {

    @Test
    @DisplayName("should should ignore and return self when null condition")
    void shouldShouldIgnoreAndReturnSelfWhenNullCondition() {
      ConditionBuilder builder = ConditionBuilder.builder();

      ConditionBuilder result = builder.and(null);

      assertThat(result).isSameAs(builder);
      assertThat(builder.build()).isEqualTo(DSL.noCondition());
    }

    @Test
    @DisplayName("should should set condition when first condition")
    void shouldShouldSetConditionWhenFirstCondition() {
      Condition condition = ID_FIELD.eq(1L);

      Condition built = ConditionBuilder.builder().and(condition).build();

      assertThat(built).isEqualTo(condition);
    }

    @Test
    @DisplayName("should should combine with and when multiple conditions")
    void shouldShouldCombineWithAndWhenMultipleConditions() {
      Condition first = ID_FIELD.eq(1L);
      Condition second = NAME_FIELD.eq("admin");

      Condition built = ConditionBuilder.builder().and(first).and(second).build();

      assertThat(built).isEqualTo(first.and(second));
    }
  }

  @Nested
  @DisplayName("when combining conditions with or")
  class WhenCombiningConditionsWithOr {

    @Test
    @DisplayName("should should ignore and return self when null condition")
    void shouldShouldIgnoreAndReturnSelfWhenNullCondition() {
      ConditionBuilder builder = ConditionBuilder.builder();

      ConditionBuilder result = builder.or(null);

      assertThat(result).isSameAs(builder);
      assertThat(builder.build()).isEqualTo(DSL.noCondition());
    }

    @Test
    @DisplayName("should should set condition when first condition")
    void shouldShouldSetConditionWhenFirstCondition() {
      Condition condition = ID_FIELD.eq(1L);

      Condition built = ConditionBuilder.builder().or(condition).build();

      assertThat(built).isEqualTo(condition);
    }

    @Test
    @DisplayName("should should combine with or when multiple conditions")
    void shouldShouldCombineWithOrWhenMultipleConditions() {
      Condition first = ID_FIELD.eq(1L);
      Condition second = NAME_FIELD.eq("admin");

      Condition built = ConditionBuilder.builder().or(first).or(second).build();

      assertThat(built).isEqualTo(first.or(second));
    }
  }

  @Test
  @DisplayName("should should return noCondition when no conditions")
  void shouldShouldReturnNoConditionWhenNoConditions() {
    Condition built = ConditionBuilder.builder().build();

    assertThat(built).isEqualTo(DSL.noCondition());
  }

  @Test
  @DisplayName("should should return built condition when conditions")
  void shouldShouldReturnBuiltConditionWhenConditions() {
    Condition condition = ID_FIELD.gt(0L);

    Condition built = ConditionBuilder.builder().and(condition).build();

    assertThat(built).isEqualTo(condition);
  }

  @Test
  @DisplayName("should should return null when no conditions")
  void shouldShouldReturnNullWhenNoConditions() {
    Condition nullable = ConditionBuilder.builder().nullable();

    assertThat(nullable).isNull();
  }

  @Test
  @DisplayName("should should return built condition when nullable with conditions")
  void shouldShouldReturnBuiltConditionWhenNullableWithConditions() {
    Condition condition = ID_FIELD.eq(42L);

    Condition nullable = ConditionBuilder.builder().and(condition).nullable();

    assertThat(nullable).isEqualTo(condition);
  }
}
