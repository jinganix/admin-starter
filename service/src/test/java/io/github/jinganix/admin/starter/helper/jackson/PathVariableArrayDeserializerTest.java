package io.github.jinganix.admin.starter.helper.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;

@DisplayName("PathVariableArrayDeserializer")
class PathVariableArrayDeserializerTest {

  private JsonMapper jsonMapper;

  private JavaType listType;

  private PathVariableArrayDeserializer<String> deserializer;

  @BeforeEach
  void setup() {
    jsonMapper = new JsonMapper();
    listType = jsonMapper.getTypeFactory().constructCollectionType(List.class, String.class);
    deserializer = new PathVariableArrayDeserializer<>(listType);
  }

  @Nested
  @DisplayName("when deserializing path variable array")
  class WhenDeserializingPathVariableArray {

    @Test
    @DisplayName("should should parse list when comma-separated values")
    void shouldShouldParseListWhenCommaSeparatedValues() throws Exception {
      List<String> result = deserialize("\"one,two,three\"");

      assertThat(result).containsExactly("one", "two", "three");
    }

    @Test
    @DisplayName("should should parse list directly when json array string")
    void shouldShouldParseListDirectlyWhenJsonArrayString() throws Exception {
      List<String> result = deserialize("\"[\\\"a\\\",\\\"b\\\"]\"");

      assertThat(result).containsExactly("a", "b");
    }

    @Test
    @DisplayName("should should preserve quotes in output when quoted tokens")
    void shouldShouldPreserveQuotesInOutputWhenQuotedTokens() throws Exception {
      List<String> result = deserialize("\"\\\"x\\\",y\"");

      assertThat(result).hasSize(2);
      assertThat(result.get(0)).isEqualTo("x");
    }

    @Test
    @DisplayName("should should throw illegal state when missing target type")
    void shouldShouldThrowIllegalStateWhenMissingTargetType() {
      PathVariableArrayDeserializer<String> untyped = new PathVariableArrayDeserializer<>();

      assertThatThrownBy(() -> deserialize(untyped, "\"a,b\""))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Target type cannot be determined");
    }

    @Test
    @DisplayName("should should parse singleton list when single value")
    void shouldShouldParseSingletonListWhenSingleValue() throws Exception {
      List<String> result = deserialize("\"only\"");

      assertThat(result).containsExactly("only");
    }

    @Test
    @DisplayName("should should build array json when raw comma separated values")
    void shouldShouldBuildArrayJsonWhenRawCommaSeparatedValues() {
      assertThat(deserializer.convertToArrayJson("a,b")).isEqualTo("[\"a\",\"b\"]");
    }

    @Test
    @DisplayName("should should return unchanged when json array literal")
    void shouldShouldReturnUnchangedWhenJsonArrayLiteral() {
      assertThat(deserializer.convertToArrayJson("[\"a\",\"b\"]")).isEqualTo("[\"a\",\"b\"]");
    }

    @Test
    @DisplayName("should should build array json when partial array prefix")
    void shouldShouldBuildArrayJsonWhenPartialArrayPrefix() {
      assertThat(deserializer.convertToArrayJson("[a,b")).contains("a").contains("b");
    }

    @Test
    @DisplayName("should should parse integer list when comma-separated integers")
    void shouldShouldParseIntegerListWhenCommaSeparatedIntegers() throws Exception {
      JavaType integerListType =
          jsonMapper.getTypeFactory().constructCollectionType(List.class, Integer.class);
      PathVariableArrayDeserializer<Integer> integerDeserializer =
          new PathVariableArrayDeserializer<>(integerListType);

      List<Integer> result = deserialize(integerDeserializer, integerListType, "\"1,2,3\"");

      assertThat(result).containsExactly(1, 2, 3);
    }
  }

  @Nested
  @DisplayName("when creating contextual deserializer")
  class WhenCreatingContextualDeserializer {

    @Test
    @DisplayName("should should return typed deserializer when bean property")
    void shouldShouldReturnTypedDeserializerWhenBeanProperty() throws Exception {
      BeanProperty property = mock(BeanProperty.class);
      when(property.getType()).thenReturn(listType);
      PathVariableArrayDeserializer<String> untyped = new PathVariableArrayDeserializer<>();

      ValueDeserializer<?> contextual =
          untyped.createContextual(mock(DeserializationContext.class), property);

      assertThat(contextual).isInstanceOf(PathVariableArrayDeserializer.class);
      @SuppressWarnings("unchecked")
      List<String> result =
          deserialize((PathVariableArrayDeserializer<String>) contextual, "\"a,b\"");
      assertThat(result).containsExactly("a", "b");
    }

    @Test
    @DisplayName("should should return same instance when null property")
    void shouldShouldReturnSameInstanceWhenNullProperty() {
      PathVariableArrayDeserializer<String> typed = new PathVariableArrayDeserializer<>(listType);

      ValueDeserializer<?> contextual =
          typed.createContextual(mock(DeserializationContext.class), null);

      assertThat(contextual).isSameAs(typed);
    }
  }

  private List<String> deserialize(String json) throws Exception {
    return deserialize(deserializer, listType, json);
  }

  private List<String> deserialize(PathVariableArrayDeserializer<String> target, String json)
      throws Exception {
    return deserialize(target, listType, json);
  }

  private <T> List<T> deserialize(
      PathVariableArrayDeserializer<T> target, JavaType type, String json) throws Exception {
    ObjectReader reader = jsonMapper.readerFor(type);
    try (JsonParser parser = reader.createParser(json)) {
      parser.nextToken();
      DeserializationContext context = mock(DeserializationContext.class);
      return target.deserialize(parser, context);
    }
  }
}
