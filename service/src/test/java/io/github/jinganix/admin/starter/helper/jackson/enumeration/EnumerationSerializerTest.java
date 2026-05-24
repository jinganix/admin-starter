package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

@DisplayName("EnumerationSerializer")
class EnumerationSerializerTest {

  private EnumerationSerializer serializer;

  private JsonGenerator jsonGenerator;

  private SerializationContext serializationContext;

  @BeforeEach
  void setup() {
    serializer = new EnumerationSerializer();
    jsonGenerator = mock(JsonGenerator.class);
    serializationContext = mock(SerializationContext.class);
  }

  @Test
  @DisplayName("should should write number when integer enumeration")
  void shouldShouldWriteNumberWhenIntegerEnumeration() throws Exception {
    // When
    serializer.serialize(PermissionType.GROUP, jsonGenerator, serializationContext);

    // Then
    verify(jsonGenerator).writeNumber(0);
  }

  @Test
  @DisplayName("should should write string when string enumeration")
  void shouldShouldWriteStringWhenStringEnumeration() throws Exception {
    // When
    serializer.serialize(Authority.ADM, jsonGenerator, serializationContext);

    // Then
    verify(jsonGenerator).writeString("/adm/");
  }

  @Test
  @DisplayName("should should throw illegal argument when unsupported value type")
  void shouldShouldThrowIllegalArgumentWhenUnsupportedValueType() {
    // Given
    @SuppressWarnings("unchecked")
    Enumeration<?> unsupported = mock(Enumeration.class);
    when(unsupported.getValue()).thenReturn(Boolean.TRUE);

    // When / Then
    assertThatThrownBy(() -> serializer.serialize(unsupported, jsonGenerator, serializationContext))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
