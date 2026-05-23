package io.github.jinganix.admin.starter.sys.role.handler;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePb;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePermission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.lib.pageable.SortDirection;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RolePb;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("RoleListHandler")
class RoleListHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleListHandler roleListHandler;

  RolePb role1 = rolePb(UID_1, List.of(UID_3));

  RolePb role2 = rolePb(UID_2, emptyList());

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given no roles -> return empty list")
  void givenNoRoles() {
    // Given
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    RoleListRequest request =
        new RoleListRequest(new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null);

    // When
    RoleListResponse response = roleListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(new RoleListResponse(emptyList())));
  }

  @Test
  @DisplayName("Given roles order by id desc -> return UID_2 first")
  void givenRolesOrderByIdDesc() {
    // Given
    testHelper.insertEntities(role(UID_1), role(UID_2), rolePermission(UID_3, UID_1, UID_3));
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    RoleListRequest request =
        new RoleListRequest(new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null);

    // When
    RoleListResponse response = roleListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(2, new RoleListResponse(List.of(role2, role1))).setPages(1));
  }
}
