package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnumValuesMap")
class EnumValuesMapTest {

  @Test
  @DisplayName("should should expose value maps when enumeration classes")
  void shouldShouldExposeValueMapsWhenEnumerationClasses() {
    Map<Class<?>, Map<Object, Enumeration<?>>> valuesMap = EnumValuesMap.getValuesMap();

    assertThat(valuesMap).containsKey(PermissionType.class);
    assertThat(EnumValuesMap.getValueMap(PermissionType.class)).containsKey("1");
    assertThat(EnumValuesMap.getValueMap(PermissionType.class).get("1"))
        .isEqualTo(PermissionType.API);
  }

  @Test
  @DisplayName("should should expose proto enumeration value maps when proto enumeration classes")
  void shouldShouldExposeProtoEnumerationValueMapsWhenProtoEnumerationClasses() {
    Class<?> protoPermissionType =
        io.github.jinganix.admin.starter.proto.sys.permission.PermissionType.class;

    assertThat(EnumValuesMap.getValuesMap()).containsKey(protoPermissionType);
    assertThat(EnumValuesMap.getValueMap(protoPermissionType)).containsKey(2);
    assertThat(EnumValuesMap.getValueMap(protoPermissionType).get(2))
        .isEqualTo(io.github.jinganix.admin.starter.proto.sys.permission.PermissionType.UI);
  }
}
