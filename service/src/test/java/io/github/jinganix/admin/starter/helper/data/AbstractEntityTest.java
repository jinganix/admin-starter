package io.github.jinganix.admin.starter.helper.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AbstractEntity")
class AbstractEntityTest {

  private TestEntity entity;

  @BeforeEach
  void setup() {
    entity = new TestEntity();
  }

  @Test
  @DisplayName("should getCreatedAt should return it when timestamp")
  void shouldGetCreatedAtShouldReturnItWhenTimestamp() {
    entity.setCreatedAt(100L);

    assertThat(entity.getCreatedAt()).isEqualTo(100L);
  }

  @Test
  @DisplayName("should should return null when unset field")
  void shouldShouldReturnNullWhenUnsetCreatedAtField() {
    assertThat(entity.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("should getUpdatedAt should return it when timestamp")
  void shouldGetUpdatedAtShouldReturnItWhenTimestamp() {
    entity.setUpdatedAt(200L);

    assertThat(entity.getUpdatedAt()).isEqualTo(200L);
  }

  @Test
  @DisplayName("should should return null when unset updatedAt field")
  void shouldShouldReturnNullWhenUnsetUpdatedAtField() {
    assertThat(entity.getUpdatedAt()).isNull();
  }

  private static final class TestEntity extends AbstractEntity {}
}
