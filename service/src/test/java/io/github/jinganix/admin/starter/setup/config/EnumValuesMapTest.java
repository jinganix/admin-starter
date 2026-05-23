package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EnumValuesMap")
class EnumValuesMapTest {

  @Nested
  @DisplayName("getValuesMap")
  class GetValuesMap {

    @Test
    @DisplayName("Given enumeration classes -> should expose value maps")
    void givenEnumerationClassesShouldExposeValueMaps() {
      Map<Class<?>, Map<Object, Enumeration<?>>> valuesMap = EnumValuesMap.getValuesMap();

      assertThat(valuesMap).containsKey(PermissionType.class);
      assertThat(EnumValuesMap.getValueMap(PermissionType.class)).containsKey("1");
      assertThat(EnumValuesMap.getValueMap(PermissionType.class).get("1"))
          .isEqualTo(PermissionType.API);
    }
  }
}
