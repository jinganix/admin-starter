package io.github.jinganix.admin.starter.helper.redisson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.netty.buffer.ByteBuf;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

@DisplayName("JsonCodec")
class JsonCodecTest {

  @Nested
  @DisplayName("when encoding values")
  class WhenEncodingValues {

    @Test
    @DisplayName("should should round-trip value when class constructor")
    void shouldShouldRoundTripValueWhenClassConstructor() throws Exception {
      JsonCodec codec = new JsonCodec(String.class);
      ByteBuf encoded = codec.getValueEncoder().encode("hello");

      try {
        Object decoded = codec.getValueDecoder().decode(encoded, null);

        assertThat(decoded).isEqualTo("hello");
      } finally {
        encoded.release();
      }
    }

    @Test
    @DisplayName("should should round-trip value when type reference constructor")
    void shouldShouldRoundTripValueWhenTypeReferenceConstructor() throws Exception {
      JsonCodec codec = new JsonCodec(new TypeReference<List<String>>() {});
      List<String> value = List.of("a", "b");
      ByteBuf encoded = codec.getValueEncoder().encode(value);

      try {
        @SuppressWarnings("unchecked")
        List<String> decoded = (List<String>) codec.getValueDecoder().decode(encoded, null);

        assertThat(decoded).containsExactly("a", "b");
      } finally {
        encoded.release();
      }
    }

    @Test
    @DisplayName("should should throw when non-serializable value")
    void shouldShouldThrowWhenNonSerializableValue() {
      JsonCodec codec = new JsonCodec(BadBean.class);

      assertThatThrownBy(() -> codec.getValueEncoder().encode(new BadBean()))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("boom");
    }
  }

  @Test
  @DisplayName("should should decode to typed value when encoded json bytes")
  void shouldShouldDecodeToTypedValueWhenEncodedJsonBytes() throws Exception {
    JsonCodec codec = new JsonCodec(Integer.class);
    ByteBuf encoded = codec.getValueEncoder().encode(42);

    try {
      Object decoded = codec.getValueDecoder().decode(encoded, null);

      assertThat(decoded).isEqualTo(42);
    } finally {
      encoded.release();
    }
  }

  static class BadBean {

    public String getValue() {
      throw new RuntimeException("boom");
    }
  }
}
