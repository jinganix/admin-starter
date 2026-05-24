package io.github.jinganix.admin.starter.sys.permission.repository;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DisplayName("PermissionRepository")
class PermissionRepositoryTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionRepository permissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("filter")
  class Filter {

    @Test
    @DisplayName("Given code filter only -> returns matching permissions")
    void givenCodeFilterOnly() {
      testHelper.insertEntities(
          permission(UID_1).setCode("alpha-code-1"), permission(UID_2).setCode("beta-code-2"));

      Page<?> page = permissionRepository.filter(PageRequest.of(0, 20), "alpha", null, null);

      assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Given status filter only -> returns matching permissions")
    void givenStatusFilterOnly() {
      testHelper.insertEntities(
          permission(UID_1).setCode("status-alpha").setStatus(PermissionStatus.ACTIVE),
          permission(UID_2).setCode("status-beta").setStatus(PermissionStatus.INACTIVE));

      Page<?> page =
          permissionRepository.filter(PageRequest.of(0, 20), null, PermissionStatus.INACTIVE, null);

      assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Given type filter only -> returns matching permissions")
    void givenTypeFilterOnly() {
      testHelper.insertEntities(
          permission(UID_1).setCode("type-api").setType(PermissionType.API),
          permission(UID_2).setCode("type-ui").setType(PermissionType.UI));

      Page<?> page =
          permissionRepository.filter(
              PageRequest.of(0, 20), null, null, List.of(PermissionType.UI));

      assertThat(page.getTotalElements()).isEqualTo(1);
    }
  }
}
