package io.github.jinganix.admin.starter.sys.role;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePb;
import static io.github.jinganix.admin.starter.tests.InvalidRequestCase.badRequest;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.proto.lib.option.OptionStringPb;
import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleOptionsRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleOptionsResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RolePb;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleStatus;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.tests.InvalidRequestCase;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleController")
class RoleControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when create request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenCreateRequestIsInvalid {

    private Stream<InvalidRequestCase<RoleCreateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              (RoleCreateRequest)
                  new RoleCreateRequest().setCode("code").setStatus(RoleStatus.ACTIVE),
              "should return bad request when name is null"),
          badRequest(
              (RoleCreateRequest)
                  new RoleCreateRequest()
                      .setName("ab")
                      .setCode("code")
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when name below min length (3)"),
          badRequest(
              (RoleCreateRequest)
                  new RoleCreateRequest()
                      .setName("a".repeat(41))
                      .setCode("code")
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when name above max length (40)"),
          badRequest(
              (RoleCreateRequest)
                  new RoleCreateRequest().setName("name").setStatus(RoleStatus.ACTIVE),
              "should return bad request when code is null"),
          badRequest(
              (RoleCreateRequest)
                  new RoleCreateRequest()
                      .setName("name")
                      .setCode("ab")
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when code below min length (3)"),
          badRequest(
              (RoleCreateRequest)
                  new RoleCreateRequest()
                      .setName("name")
                      .setCode("c".repeat(21))
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when code above max length (20)"),
          badRequest(
              (RoleCreateRequest) new RoleCreateRequest().setName("name").setCode("code"),
              "should return bad request when status is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<RoleCreateRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_CREATE permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleCreatePermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(
            UID_1,
            (RoleCreateRequest)
                new RoleCreateRequest()
                    .setName("new-role")
                    .setCode("new-code")
                    .setStatus(RoleStatus.ACTIVE)
                    .setPermissionIds(Collections.emptyList()))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return ok when SYS_ROLE_CREATE permission")
  void shouldReturnOkWhenSysRoleCreatePermission() throws Exception {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_2);
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_CREATE));

    // When / Then
    testHelper
        .request(
            UID_1,
            (RoleCreateRequest)
                new RoleCreateRequest()
                    .setName("new-role")
                    .setCode("new-code")
                    .setStatus(RoleStatus.ACTIVE)
                    .setPermissionIds(Collections.emptyList()))
        .andExpect(status().isOk());
  }

  @Nested
  @DisplayName("when delete request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenDeleteRequestIsInvalid {

    private Stream<InvalidRequestCase<RoleDeleteRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new RoleDeleteRequest(), "should return bad request when ids is null"),
          badRequest(new RoleDeleteRequest(null), "should return bad request when ids is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<RoleDeleteRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_DELETE permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleDeletePermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new RoleDeleteRequest(java.util.List.of(UID_2)))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return ok when SYS_ROLE_DELETE permission")
  void shouldReturnOkWhenSysRoleDeletePermission() throws Exception {
    // Given
    testHelper.insertEntities(role(UID_2));
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_DELETE));

    // When / Then
    testHelper
        .request(UID_1, new RoleDeleteRequest(java.util.List.of(UID_2)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_LIST permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleListPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

    // When / Then
    testHelper
        .request(UID_1, new RoleListRequest(new PageablePb(), null, null))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return role list when SYS_ROLE_LIST permission")
  void shouldReturnRoleListWhenSysRoleListPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_LIST));
    testHelper.insertEntities(role(UID_1), role(UID_2));
    RolePb role1 = rolePb(UID_1);
    RolePb role2 = rolePb(UID_2);

    // When / Then
    testHelper
        .request(UID_1, new RoleListRequest(new PageablePb(), null, null))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                testHelper
                    .paging(2, new RoleListResponse(java.util.List.of(role1, role2)))
                    .setPages(1)));
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_OPTIONS permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleOptionsPermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new RoleOptionsRequest())
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return role options when SYS_ROLE_OPTIONS permission")
  void shouldReturnRoleOptionsWhenSysRoleOptionsPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_OPTIONS));
    testHelper.insertEntities(
        role(UID_1),
        role(UID_2).setStatus(io.github.jinganix.admin.starter.sys.role.model.RoleStatus.INACTIVE));

    // When / Then
    testHelper
        .request(UID_1, new RoleOptionsRequest())
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                new RoleOptionsResponse(
                    java.util.List.of(new OptionStringPb("Role 10001", UID_1 + "")))));
  }

  @Nested
  @DisplayName("when retrieve request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenRetrieveRequestIsInvalid {

    private Stream<InvalidRequestCase<RoleRetrieveRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new RoleRetrieveRequest(), "should return bad request when id is null"),
          badRequest(new RoleRetrieveRequest(null), "should return bad request when id is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(
        InvalidRequestCase<RoleRetrieveRequest> testCase) throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_GET));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_GET permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleGetPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

    // When / Then
    testHelper
        .request(UID_1, new RoleRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_1)))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return role when SYS_ROLE_GET permission")
  void shouldReturnRoleWhenSysRoleGetPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_GET));
    testHelper.insertEntities(role(UID_1));

    // When / Then
    testHelper
        .request(UID_1, new RoleRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_1)))
        .andExpect(status().isOk())
        .andExpect(testHelper.isResponse(new RoleRetrieveResponse(rolePb(UID_1))));
  }

  @Nested
  @DisplayName("when update request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenUpdateRequestIsInvalid {

    private Stream<InvalidRequestCase<RoleUpdateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              (RoleUpdateRequest)
                  new RoleUpdateRequest(UID_2).setCode("code").setStatus(RoleStatus.ACTIVE),
              "should return bad request when name is null"),
          badRequest(
              (RoleUpdateRequest)
                  new RoleUpdateRequest(UID_2)
                      .setName("ab")
                      .setCode("code")
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when name below min length (3)"),
          badRequest(
              (RoleUpdateRequest)
                  new RoleUpdateRequest(UID_2)
                      .setName("a".repeat(41))
                      .setCode("code")
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when name above max length (40)"),
          badRequest(
              (RoleUpdateRequest)
                  new RoleUpdateRequest(UID_2).setName("name").setStatus(RoleStatus.ACTIVE),
              "should return bad request when code is null"),
          badRequest(
              (RoleUpdateRequest)
                  new RoleUpdateRequest(UID_2)
                      .setName("name")
                      .setCode("ab")
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when code below min length (3)"),
          badRequest(
              (RoleUpdateRequest)
                  new RoleUpdateRequest(UID_2)
                      .setName("name")
                      .setCode("c".repeat(21))
                      .setStatus(RoleStatus.ACTIVE),
              "should return bad request when code above max length (20)"),
          badRequest(
              (RoleUpdateRequest) new RoleUpdateRequest(UID_2).setName("name").setCode("code"),
              "should return bad request when status is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<RoleUpdateRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_UPDATE permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleUpdatePermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(
            UID_1,
            new RoleUpdateRequest(UID_2)
                .setName("updated-name")
                .setCode("updated-code")
                .setStatus(RoleStatus.ACTIVE)
                .setPermissionIds(Collections.emptyList()))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return updated role when SYS_ROLE_UPDATE permission")
  void shouldReturnUpdatedRoleWhenSysRoleUpdatePermission() throws Exception {
    // Given
    testHelper.insertEntities(role(UID_2));
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_UPDATE));
    RolePb expectedRole = rolePb(UID_2, Collections.emptyList());
    expectedRole.setCode("updated-code");
    expectedRole.setName("Updated Name");
    expectedRole.setDescription("updated description");
    expectedRole.setStatus(RoleStatus.INACTIVE);

    // When / Then
    testHelper
        .request(
            UID_1,
            new RoleUpdateRequest(UID_2)
                .setCode("updated-code")
                .setName("Updated Name")
                .setDescription("updated description")
                .setStatus(RoleStatus.INACTIVE)
                .setPermissionIds(Collections.emptyList()))
        .andExpect(status().isOk())
        .andExpect(testHelper.isResponse(new RoleUpdateResponse(expectedRole)));
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_ROLE_STATUS permission")
  void shouldReturnAccessDeniedWhenMissingSysRoleStatusPermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new RoleUpdateStatusRequest(UID_2, RoleStatus.INACTIVE))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return updated status when SYS_ROLE_STATUS permission")
  void shouldReturnUpdatedStatusWhenSysRoleStatusPermission() throws Exception {
    // Given
    testHelper.insertEntities(role(UID_2));
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_ROLE_STATUS));

    // When / Then
    testHelper
        .request(UID_1, new RoleUpdateStatusRequest(UID_2, RoleStatus.INACTIVE))
        .andExpect(status().isOk())
        .andExpect(testHelper.isResponse(new RoleUpdateStatusResponse(UID_2, RoleStatus.INACTIVE)));
  }
}
