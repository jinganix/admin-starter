package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
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

@DisplayName("EnumerationDeserializer")
class EnumerationDeserializerTest {

  private JsonMapper jsonMapper;

  private JavaType permissionType;

  private ValueDeserializer<PermissionType> deserializer;

  @BeforeEach
  void setup() throws Exception {
    jsonMapper = new JsonMapper();
    permissionType = jsonMapper.getTypeFactory().constructType(PermissionType.class);
    EnumerationDeserializer<PermissionType> raw = new EnumerationDeserializer<>();
    DeserializationContext context = mock(DeserializationContext.class);
    JavaType contextualType = mock(JavaType.class);
    when(context.getContextualType()).thenReturn(contextualType);
    when(contextualType.getRawClass()).thenReturn((Class) PermissionType.class);
    @SuppressWarnings("unchecked")
    ValueDeserializer<PermissionType> contextual =
        (ValueDeserializer<PermissionType>) raw.createContextual(context, mock(BeanProperty.class));
    deserializer = contextual;
  }

  @Nested
  @DisplayName("deserialize")
  class Deserialize {

    @Test
    @DisplayName("Given string value -> should resolve enumeration")
    void givenStringValueShouldResolveEnumeration() throws Exception {
      PermissionType result = deserialize("\"1\"");

      assertThat(result).isEqualTo(PermissionType.API);
    }

    @Test
    @DisplayName("Given integer value -> should resolve enumeration")
    void givenIntegerValueShouldResolveEnumeration() throws Exception {
      PermissionType result = deserialize("2");

      assertThat(result).isEqualTo(PermissionType.UI);
    }

    @Test
    @DisplayName("Given unknown value -> should return null")
    void givenUnknownValueShouldReturnNull() throws Exception {
      PermissionType result = deserialize("\"unknown\"");

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Given unsupported token -> should return null")
    void givenUnsupportedTokenShouldReturnNull() throws Exception {
      PermissionType result = deserialize("true");

      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("createContextual")
  class CreateContextual {

    @Test
    @DisplayName("Given contextual type -> should resolve enumeration from enum map")
    void givenContextualTypeShouldResolveEnumerationFromEnumMap() throws Exception {
      PermissionType result = deserialize("0");

      assertThat(result).isEqualTo(PermissionType.GROUP);
    }
  }

  private PermissionType deserialize(String json) throws Exception {
    ObjectReader reader = jsonMapper.readerFor(permissionType);
    try (JsonParser parser = reader.createParser(json)) {
      parser.nextToken();
      DeserializationContext context = mock(DeserializationContext.class);
      return deserializer.deserialize(parser, context);
    }
  }
}
