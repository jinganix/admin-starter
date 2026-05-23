package io.github.jinganix.admin.starter.helper.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AbstractEntity")
class AbstractEntityTest {

  private TestEntity entity;

  @BeforeEach
  void setup() {
    entity = new TestEntity();
  }

  @Nested
  @DisplayName("setCreatedAt")
  class SetCreatedAt {

    @Test
    @DisplayName("Given timestamp -> getCreatedAt should return it")
    void givenTimestampGetCreatedAtShouldReturnIt() {
      entity.setCreatedAt(100L);

      assertThat(entity.getCreatedAt()).isEqualTo(100L);
    }
  }

  @Nested
  @DisplayName("getCreatedAt")
  class GetCreatedAt {

    @Test
    @DisplayName("Given unset field -> should return null")
    void givenUnsetFieldShouldReturnNull() {
      assertThat(entity.getCreatedAt()).isNull();
    }
  }

  @Nested
  @DisplayName("setUpdatedAt")
  class SetUpdatedAt {

    @Test
    @DisplayName("Given timestamp -> getUpdatedAt should return it")
    void givenTimestampGetUpdatedAtShouldReturnIt() {
      entity.setUpdatedAt(200L);

      assertThat(entity.getUpdatedAt()).isEqualTo(200L);
    }
  }

  @Nested
  @DisplayName("getUpdatedAt")
  class GetUpdatedAt {

    @Test
    @DisplayName("Given unset field -> should return null")
    void givenUnsetFieldShouldReturnNull() {
      assertThat(entity.getUpdatedAt()).isNull();
    }
  }

  private static final class TestEntity extends AbstractEntity {}
}
