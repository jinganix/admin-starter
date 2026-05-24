package io.github.jinganix.admin.starter.helper.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
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

@DisplayName("PathVariableMapDeserializer")
class PathVariableMapDeserializerTest {

  private JsonMapper jsonMapper;

  private JavaType mapType;

  private PathVariableMapDeserializer<String, String> deserializer;

  @BeforeEach
  void setup() {
    jsonMapper = new JsonMapper();
    mapType =
        jsonMapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
    deserializer = new PathVariableMapDeserializer<>(mapType);
  }

  @Nested
  @DisplayName("deserialize")
  class Deserialize {

    @Test
    @DisplayName("Given semicolon-separated pairs -> should parse map")
    void givenSemicolonSeparatedPairsShouldParseMap() throws Exception {
      Map<String, String> result = deserialize("\"a,1;b,2\"");

      assertThat(result).containsEntry("a", "1").containsEntry("b", "2");
    }

    @Test
    @DisplayName("Given json object string -> should parse map directly")
    void givenJsonObjectStringShouldParseMapDirectly() throws Exception {
      Map<String, String> result = deserialize("\"{\\\"x\\\":\\\"y\\\"}\"");

      assertThat(result).containsEntry("x", "y");
    }

    @Test
    @DisplayName("Given malformed pair -> should throw")
    void givenMalformedPairShouldThrow() {
      assertThatThrownBy(() -> deserialize("\"a,1;b\"")).hasMessageContaining("Bad value");
    }

    @Test
    @DisplayName("Given missing target type -> should throw illegal state")
    void givenMissingTargetTypeShouldThrowIllegalState() {
      PathVariableMapDeserializer<String, String> untyped = new PathVariableMapDeserializer<>();

      assertThatThrownBy(() -> deserialize(untyped, "\"a,1\""))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Target type cannot be determined");
    }

    @Test
    @DisplayName("Given single pair -> should parse map")
    void givenSinglePairShouldParseMap() throws Exception {
      Map<String, String> result = deserialize("\"a,1\"");

      assertThat(result).containsEntry("a", "1");
    }

    @Test
    @DisplayName("Given quoted key and value -> should preserve quotes in output")
    void givenQuotedKeyAndValueShouldPreserveQuotesInOutput() throws Exception {
      Map<String, String> result = deserialize("\"\\\"k\\\",\\\"v\\\"\"");

      assertThat(result).containsEntry("k", "v");
    }

    @Test
    @DisplayName("Given pair with extra comma -> should throw")
    void givenPairWithExtraCommaShouldThrow() {
      assertThatThrownBy(() -> deserialize("\"a,1,2\"")).hasMessageContaining("Bad value");
    }

    @Test
    @DisplayName("Given raw pair -> should build object json")
    void givenRawPairShouldBuildObjectJson() throws Exception {
      ObjectReader reader = jsonMapper.readerFor(mapType);
      try (JsonParser parser = reader.createParser("\"x\"")) {
        parser.nextToken();

        assertThat(deserializer.convertToArrayJson(parser, "a,1")).isEqualTo("{\"a\":\"1\"}");
      }
    }

    @Test
    @DisplayName("Given json object literal -> should return unchanged")
    void givenJsonObjectLiteralShouldReturnUnchanged() throws Exception {
      ObjectReader reader = jsonMapper.readerFor(mapType);
      try (JsonParser parser = reader.createParser("\"x\"")) {
        parser.nextToken();

        assertThat(deserializer.convertToArrayJson(parser, "{\"a\":\"1\"}"))
            .isEqualTo("{\"a\":\"1\"}");
      }
    }

    @Test
    @DisplayName("Given partial object prefix -> should build object json")
    void givenPartialObjectPrefixShouldBuildObjectJson() throws Exception {
      ObjectReader reader = jsonMapper.readerFor(mapType);
      try (JsonParser parser = reader.createParser("\"x\"")) {
        parser.nextToken();

        assertThat(deserializer.convertToArrayJson(parser, "{a,1")).contains("a").contains("1");
      }
    }
  }

  @Nested
  @DisplayName("createContextual")
  class CreateContextual {

    @Test
    @DisplayName("Given bean property -> should return typed deserializer")
    void givenBeanPropertyShouldReturnTypedDeserializer() throws Exception {
      BeanProperty property = mock(BeanProperty.class);
      when(property.getType()).thenReturn(mapType);
      PathVariableMapDeserializer<String, String> untyped = new PathVariableMapDeserializer<>();

      ValueDeserializer<?> contextual =
          untyped.createContextual(mock(DeserializationContext.class), property);

      assertThat(contextual).isInstanceOf(PathVariableMapDeserializer.class);
      @SuppressWarnings("unchecked")
      Map<String, String> result =
          deserialize((PathVariableMapDeserializer<String, String>) contextual, "\"a,1;b,2\"");
      assertThat(result).containsEntry("a", "1").containsEntry("b", "2");
    }

    @Test
    @DisplayName("Given null property -> should return same instance")
    void givenNullPropertyShouldReturnSameInstance() {
      PathVariableMapDeserializer<String, String> typed =
          new PathVariableMapDeserializer<>(mapType);

      ValueDeserializer<?> contextual =
          typed.createContextual(mock(DeserializationContext.class), null);

      assertThat(contextual).isSameAs(typed);
    }
  }

  private Map<String, String> deserialize(String json) throws Exception {
    return deserialize(deserializer, json);
  }

  private Map<String, String> deserialize(
      PathVariableMapDeserializer<String, String> target, String json) throws Exception {
    ObjectReader reader = jsonMapper.readerFor(mapType);
    try (JsonParser parser = reader.createParser(json)) {
      parser.nextToken();
      DeserializationContext context = mock(DeserializationContext.class);
      return target.deserialize(parser, context);
    }
  }
}
