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

  @Nested
  @DisplayName("builder")
  class Builder {

    @Test
    @DisplayName("Given builder call -> should return new instance")
    void givenBuilderCallShouldReturnNewInstance() {
      ConditionBuilder builder = ConditionBuilder.builder();

      assertThat(builder).isNotNull();
      assertThat(builder.build()).isEqualTo(DSL.noCondition());
      assertThat(builder.nullable()).isNull();
    }
  }

  @Nested
  @DisplayName("and")
  class And {

    @Test
    @DisplayName("Given null condition -> should ignore and return self")
    void givenNullConditionShouldIgnoreAndReturnSelf() {
      ConditionBuilder builder = ConditionBuilder.builder();

      ConditionBuilder result = builder.and(null);

      assertThat(result).isSameAs(builder);
      assertThat(builder.build()).isEqualTo(DSL.noCondition());
    }

    @Test
    @DisplayName("Given first condition -> should set condition")
    void givenFirstConditionShouldSetCondition() {
      Condition condition = ID_FIELD.eq(1L);

      Condition built = ConditionBuilder.builder().and(condition).build();

      assertThat(built).isEqualTo(condition);
    }

    @Test
    @DisplayName("Given multiple conditions -> should combine with and")
    void givenMultipleConditionsShouldCombineWithAnd() {
      Condition first = ID_FIELD.eq(1L);
      Condition second = NAME_FIELD.eq("admin");

      Condition built = ConditionBuilder.builder().and(first).and(second).build();

      assertThat(built).isEqualTo(first.and(second));
    }
  }

  @Nested
  @DisplayName("or")
  class Or {

    @Test
    @DisplayName("Given null condition -> should ignore and return self")
    void givenNullConditionShouldIgnoreAndReturnSelf() {
      ConditionBuilder builder = ConditionBuilder.builder();

      ConditionBuilder result = builder.or(null);

      assertThat(result).isSameAs(builder);
      assertThat(builder.build()).isEqualTo(DSL.noCondition());
    }

    @Test
    @DisplayName("Given first condition -> should set condition")
    void givenFirstConditionShouldSetCondition() {
      Condition condition = ID_FIELD.eq(1L);

      Condition built = ConditionBuilder.builder().or(condition).build();

      assertThat(built).isEqualTo(condition);
    }

    @Test
    @DisplayName("Given multiple conditions -> should combine with or")
    void givenMultipleConditionsShouldCombineWithOr() {
      Condition first = ID_FIELD.eq(1L);
      Condition second = NAME_FIELD.eq("admin");

      Condition built = ConditionBuilder.builder().or(first).or(second).build();

      assertThat(built).isEqualTo(first.or(second));
    }
  }

  @Nested
  @DisplayName("build")
  class Build {

    @Test
    @DisplayName("Given no conditions -> should return noCondition")
    void givenNoConditionsShouldReturnNoCondition() {
      Condition built = ConditionBuilder.builder().build();

      assertThat(built).isEqualTo(DSL.noCondition());
    }

    @Test
    @DisplayName("Given conditions -> should return built condition")
    void givenConditionsShouldReturnBuiltCondition() {
      Condition condition = ID_FIELD.gt(0L);

      Condition built = ConditionBuilder.builder().and(condition).build();

      assertThat(built).isEqualTo(condition);
    }
  }

  @Nested
  @DisplayName("nullable")
  class Nullable {

    @Test
    @DisplayName("Given no conditions -> should return null")
    void givenNoConditionsShouldReturnNull() {
      Condition nullable = ConditionBuilder.builder().nullable();

      assertThat(nullable).isNull();
    }

    @Test
    @DisplayName("Given conditions -> should return built condition")
    void givenConditionsShouldReturnBuiltCondition() {
      Condition condition = ID_FIELD.eq(42L);

      Condition nullable = ConditionBuilder.builder().and(condition).nullable();

      assertThat(nullable).isEqualTo(condition);
    }
  }
}
