package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.json.JsonMapper;

@DisplayName("EnumerationKeyDeserializer")
class EnumerationKeyDeserializerTest {

  private final JsonMapper jsonMapper = new JsonMapper();

  @Test
  @DisplayName("should should resolve enumeration when known key")
  void shouldShouldResolveEnumerationWhenKnownKey() throws Exception {
    EnumerationKeyDeserializer deserializer = contextual(PermissionType.class);

    Object result = deserializer.deserializeKey("1", mock(DeserializationContext.class));

    assertThat(result).isEqualTo(PermissionType.API);
  }

  @Test
  @DisplayName("should should resolve key from enum map when contextual type")
  void shouldShouldResolveKeyFromEnumMapWhenContextualType() throws Exception {
    EnumerationKeyDeserializer deserializer = new EnumerationKeyDeserializer();
    DeserializationContext context = mock(DeserializationContext.class);
    JavaType mapKeyType = mock(JavaType.class);
    JavaType contextualType = mock(JavaType.class);
    when(context.getContextualType()).thenReturn(contextualType);
    when(contextualType.getKeyType()).thenReturn(mapKeyType);
    when(mapKeyType.getRawClass()).thenReturn((Class) PermissionType.class);

    KeyDeserializer contextual = deserializer.createContextual(context, mock(BeanProperty.class));

    assertThat(contextual).isSameAs(deserializer);
    assertThat(contextual.deserializeKey("2", context)).isEqualTo(PermissionType.UI);
  }

  private EnumerationKeyDeserializer contextual(Class<?> enumClass) throws Exception {
    EnumerationKeyDeserializer deserializer = new EnumerationKeyDeserializer();
    DeserializationContext context = mock(DeserializationContext.class);
    JavaType mapKeyType = jsonMapper.getTypeFactory().constructType(enumClass);
    JavaType contextualType = mock(JavaType.class);
    when(context.getContextualType()).thenReturn(contextualType);
    when(contextualType.getKeyType()).thenReturn(mapKeyType);
    deserializer.createContextual(context, mock(BeanProperty.class));
    return deserializer;
  }
}
