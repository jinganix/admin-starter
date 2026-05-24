package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permissionPb;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.lib.pageable.SortDirection;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionPb;
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

@DisplayName("PermissionListHandler")
class PermissionListHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionListHandler permissionListHandler;

  PermissionPb permission1;

  PermissionPb permission2;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
    permission1 = permissionPb(UID_1);
    permission1.setName("perm-one").setCode("/test/one").setDescription("");
    permission2 = permissionPb(UID_2);
    permission2.setName("perm-two").setCode("/test/two").setDescription("");
  }

  @Test
  @DisplayName("should return empty list when code filter with no match")
  void shouldReturnEmptyListWhenCodeFilterWithNoMatch() {
    // Given
    testHelper.insertEntities(
        permission(UID_1).setName("perm-one").setCode("/test/one"),
        permission(UID_2).setName("perm-two").setCode("/test/two"));
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    PermissionListRequest request =
        new PermissionListRequest(
            new PageablePb().setSort(Map.of("id", SortDirection.desc)), "nomatch", null, null);

    // When
    PermissionListResponse response = permissionListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(new PermissionListResponse(emptyList())));
  }

  @Test
  @DisplayName("should return UID_1 first when permissions order by id asc")
  void shouldReturnUid1FirstWhenPermissionsOrderByIdAsc() {
    // Given
    testHelper.insertEntities(
        permission(UID_1).setName("perm-one").setCode("/test/one").setDescription(""),
        permission(UID_2).setName("perm-two").setCode("/test/two").setDescription(""));
    Pageable pageable =
        PageRequest.of(0, 20, Sort.by(Sort.Order.asc("id"), Sort.Order.desc("name")));
    PermissionListRequest request =
        new PermissionListRequest(
            new PageablePb().setSort(Map.of("id", SortDirection.asc, "name", SortDirection.desc)),
            null,
            null,
            null);

    // When
    PermissionListResponse response = permissionListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            testHelper
                .paging(2, new PermissionListResponse(List.of(permission1, permission2)))
                .setPages(1));
  }
}
