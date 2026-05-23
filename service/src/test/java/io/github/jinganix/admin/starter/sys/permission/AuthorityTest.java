package io.github.jinganix.admin.starter.sys.permission;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Authority")
class AuthorityTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given valid authority value -> fromValue returns enum")
  void givenValidValue() {
    // Given
    String value = Authority.SYS_USER_LIST.getValue();

    // When
    Authority authority = Authority.fromValue(value);

    // Then
    assertThat(authority).isEqualTo(Authority.SYS_USER_LIST);
  }

  @Test
  @DisplayName("Given invalid authority value -> fromValue returns null")
  void givenInvalidValue() {
    // Given
    String value = "/sys/unknown/path";

    // When
    Authority authority = Authority.fromValue(value);

    // Then
    assertThat(authority).isNull();
  }

  @Test
  @DisplayName("Given enum constant -> getValue returns configured path")
  void givenEnumConstant() {
    // Given / When
    String value = Authority.ADM_OVERVIEW_LIST.getValue();

    // Then
    assertThat(value).isEqualTo("/adm/overview/list");
  }
}
