package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadRequest;
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
  @DisplayName("when deserializing enumeration")
  class WhenDeserializingEnumeration {

    @Test
    @DisplayName("should should resolve enumeration when string value")
    void shouldShouldResolveEnumerationWhenStringValue() throws Exception {
      PermissionType result = deserialize("\"1\"");

      assertThat(result).isEqualTo(PermissionType.API);
    }

    @Test
    @DisplayName("should should resolve enumeration when integer value")
    void shouldShouldResolveEnumerationWhenIntegerValue() throws Exception {
      PermissionType result = deserialize("2");

      assertThat(result).isEqualTo(PermissionType.UI);
    }

    @Test
    @DisplayName("should should return null when unknown value")
    void shouldShouldReturnNullWhenUnknownValue() throws Exception {
      PermissionType result = deserialize("\"unknown\"");

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("should should return null when unsupported token")
    void shouldShouldReturnNullWhenUnsupportedToken() throws Exception {
      PermissionType result = deserialize("true");

      assertThat(result).isNull();
    }
  }

  @Test
  @DisplayName("should should resolve enumeration from enum map when contextual type")
  void shouldShouldResolveEnumerationFromEnumMapWhenContextualType() throws Exception {
    PermissionType result = deserialize("0");

    assertThat(result).isEqualTo(PermissionType.GROUP);
  }

  @Test
  @DisplayName("should deserialize permission upload request when type is zero")
  void shouldDeserializePermissionUploadRequestWhenTypeIsZero() throws Exception {
    PermissionUploadRequest request =
        jsonMapper.readValue(
            """
            {"a":[{"a":"perm-name","b":"perm-code","c":0,"e":1}]}
            """,
            PermissionUploadRequest.class);

    assertThat(request.getPermissions()).hasSize(1);
    assertThat(request.getPermissions().getFirst().getType())
        .isEqualTo(io.github.jinganix.admin.starter.proto.sys.permission.PermissionType.GROUP);
    assertThat(request.getPermissions().getFirst().getStatus()).isEqualTo(PermissionStatus.ACTIVE);
  }

  @Test
  @DisplayName("should deserialize permission upload request when json uses property aliases")
  void shouldDeserializePermissionUploadRequestWhenJsonUsesPropertyAliases() throws Exception {
    PermissionUploadRequest request =
        jsonMapper.readValue(
            """
            {"a":[{"a":"perm-name","b":"perm-code","c":1,"e":1}]}
            """,
            PermissionUploadRequest.class);

    assertThat(request.getPermissions()).hasSize(1);
    assertThat(request.getPermissions().getFirst().getName()).isEqualTo("perm-name");
    assertThat(request.getPermissions().getFirst().getCode()).isEqualTo("perm-code");
    assertThat(request.getPermissions().getFirst().getType())
        .isEqualTo(io.github.jinganix.admin.starter.proto.sys.permission.PermissionType.API);
    assertThat(request.getPermissions().getFirst().getStatus()).isEqualTo(PermissionStatus.ACTIVE);
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
