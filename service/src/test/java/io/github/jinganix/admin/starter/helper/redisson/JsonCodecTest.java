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
  @DisplayName("getValueEncoder")
  class GetValueEncoder {

    @Test
    @DisplayName("Given class constructor -> should round-trip value")
    void givenClassConstructorShouldRoundTripValue() throws Exception {
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
    @DisplayName("Given type reference constructor -> should round-trip value")
    void givenTypeReferenceConstructorShouldRoundTripValue() throws Exception {
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
    @DisplayName("Given non-serializable value -> should throw")
    void givenNonSerializableValueShouldThrow() {
      JsonCodec codec = new JsonCodec(BadBean.class);

      assertThatThrownBy(() -> codec.getValueEncoder().encode(new BadBean()))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("boom");
    }
  }

  @Nested
  @DisplayName("getValueDecoder")
  class GetValueDecoder {

    @Test
    @DisplayName("Given encoded json bytes -> should decode to typed value")
    void givenEncodedJsonBytesShouldDecodeToTypedValue() throws Exception {
      JsonCodec codec = new JsonCodec(Integer.class);
      ByteBuf encoded = codec.getValueEncoder().encode(42);

      try {
        Object decoded = codec.getValueDecoder().decode(encoded, null);

        assertThat(decoded).isEqualTo(42);
      } finally {
        encoded.release();
      }
    }
  }

  static class BadBean {

    public String getValue() {
      throw new RuntimeException("boom");
    }
  }
}
