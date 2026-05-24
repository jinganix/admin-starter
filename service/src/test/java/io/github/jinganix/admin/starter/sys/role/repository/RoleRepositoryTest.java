package io.github.jinganix.admin.starter.sys.role.repository;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DisplayName("RoleRepository")
class RoleRepositoryTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleRepository roleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("filter")
  class Filter {

    @Test
    @DisplayName("Given name filter only -> returns matching roles")
    void givenNameFilterOnly() {
      testHelper.insertEntities(
          role(UID_1).setName("Alpha Role"), role(UID_2).setName("Beta Role"));

      Page<?> page = roleRepository.filter(PageRequest.of(0, 20), "Alpha", null);

      assertThat(page.getTotalElements()).isEqualTo(1);
      assertThat(page.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Given status filter only -> returns matching roles")
    void givenStatusFilterOnly() {
      testHelper.insertEntities(
          role(UID_1).setStatus(RoleStatus.ACTIVE), role(UID_2).setStatus(RoleStatus.INACTIVE));

      Page<?> page = roleRepository.filter(PageRequest.of(0, 20), null, RoleStatus.INACTIVE);

      assertThat(page.getTotalElements()).isEqualTo(1);
    }
  }
}
