package io.github.jinganix.admin.starter.sys.permission;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.CODE;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.NAME;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.createRequest;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.editPb;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permissionPb;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.updateRequest;
import static io.github.jinganix.admin.starter.tests.InvalidRequestCase.badRequest;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionsRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionsResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionReloadRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionReloadResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionType;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadResponse;
import io.github.jinganix.admin.starter.tests.InvalidRequestCase;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionController")
class PermissionControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("create")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Create {

    private Stream<InvalidRequestCase<PermissionCreateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setCode(CODE)
                      .setType(PermissionType.API)
                      .setStatus(PermissionStatus.ACTIVE),
              "name is null"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName("ab")
                      .setCode(CODE)
                      .setType(PermissionType.API)
                      .setStatus(PermissionStatus.ACTIVE),
              "name below min length (3)"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName("a".repeat(41))
                      .setCode(CODE)
                      .setType(PermissionType.API)
                      .setStatus(PermissionStatus.ACTIVE),
              "name above max length (40)"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName(NAME)
                      .setType(PermissionType.API)
                      .setStatus(PermissionStatus.ACTIVE),
              "code is null"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName(NAME)
                      .setCode("ab")
                      .setType(PermissionType.API)
                      .setStatus(PermissionStatus.ACTIVE),
              "code below min length (3)"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName(NAME)
                      .setCode("a".repeat(21))
                      .setType(PermissionType.API)
                      .setStatus(PermissionStatus.ACTIVE),
              "code above max length (20)"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName(NAME)
                      .setCode(CODE)
                      .setStatus(PermissionStatus.ACTIVE),
              "type is null"),
          badRequest(
              (PermissionCreateRequest)
                  new PermissionCreateRequest()
                      .setName(NAME)
                      .setCode(CODE)
                      .setType(PermissionType.API),
              "status is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<PermissionCreateRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_CREATE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_PERMISSION_CREATE permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(UID_1, createRequest())
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_CREATE permission -> response created")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_CREATE));
      when(uidGenerator.nextUid()).thenReturn(UID_3);

      // When / Then
      testHelper
          .request(UID_1, createRequest())
          .andExpect(status().isOk())
          .andExpect(testHelper.isResponse(new PermissionCreateResponse()));
    }
  }

  @Nested
  @DisplayName("delete")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Delete {

    private Stream<InvalidRequestCase<PermissionDeleteRequest>> invalidRequests() {
      return Stream.of(badRequest(new PermissionDeleteRequest(null), "ids is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<PermissionDeleteRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_DELETE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_PERMISSION_DELETE permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(UID_1, new PermissionDeleteRequest(java.util.List.of(UID_3)))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_DELETE permission -> response deleted")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_DELETE));
      testHelper.insertEntities(permission(UID_3));

      // When / Then
      testHelper
          .request(UID_1, new PermissionDeleteRequest(java.util.List.of(UID_3)))
          .andExpect(status().isOk())
          .andExpect(testHelper.isResponse(new PermissionDeleteResponse()));
    }
  }

  @Nested
  @DisplayName("list")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class List {

    @Test
    @DisplayName("Given missing SYS_PERMISSION_LIST permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

      // When / Then
      testHelper
          .request(UID_1, new PermissionListRequest(new PageablePb(), null, null, null))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_LIST permission -> response permission list")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_LIST));
      testHelper.insertEntities(
          permission(UID_3).setCode("perm-code-1").setName("perm-name-1"),
          permission(UID_4).setCode("perm-code-2").setName("perm-name-2"));
      PermissionPb perm3 = permissionPb(UID_3);
      perm3.setCode("perm-code-1");
      perm3.setName("perm-name-1");
      PermissionPb perm4 = permissionPb(UID_4);
      perm4.setCode("perm-code-2");
      perm4.setName("perm-name-2");

      // When / Then
      testHelper
          .request(UID_1, new PermissionListRequest(new PageablePb(), null, null, null))
          .andExpect(status().isOk())
          .andExpect(
              testHelper.isResponse(
                  testHelper
                      .paging(2, new PermissionListResponse(java.util.List.of(perm3, perm4)))
                      .setPages(1)));
    }
  }

  @Nested
  @DisplayName("reload")
  class Reload {

    @Test
    @DisplayName("Given missing SYS_PERMISSION_RELOAD permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(UID_1, new PermissionReloadRequest())
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_RELOAD permission -> response reloaded")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_RELOAD));
      AtomicLong nextId = new AtomicLong(UID_3);
      when(uidGenerator.nextUid()).thenAnswer(invocation -> nextId.getAndIncrement());

      // When / Then
      testHelper
          .request(UID_1, new PermissionReloadRequest())
          .andExpect(status().isOk())
          .andExpect(testHelper.isResponse(new PermissionReloadResponse()));
    }
  }

  @Nested
  @DisplayName("options")
  class Options {

    @Test
    @DisplayName("Given missing SYS_PERMISSION_OPTIONS permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(UID_1, new PermissionOptionsRequest())
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_OPTIONS permission -> response permission options")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_OPTIONS));
      testHelper.insertEntities(permission(UID_3));

      // When / Then
      testHelper
          .request(UID_1, new PermissionOptionsRequest())
          .andExpect(status().isOk())
          .andExpect(
              testHelper.isResponse(
                  new PermissionOptionsResponse(
                      java.util.List.of(
                          (PermissionOptionPb)
                              new PermissionOptionPb()
                                  .setCode(CODE)
                                  .setLabel(NAME)
                                  .setValue(UID_3 + "")))));
    }
  }

  @Nested
  @DisplayName("retrieve")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Retrieve {

    private Stream<InvalidRequestCase<PermissionRetrieveRequest>> invalidRequests() {
      return Stream.of(badRequest(new PermissionRetrieveRequest(null), "id is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<PermissionRetrieveRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_LIST));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_PERMISSION_LIST permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

      // When / Then
      testHelper
          .request(
              UID_1, new PermissionRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_3)))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_LIST permission -> response permission")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_LIST));
      testHelper.insertEntities(permission(UID_3));

      // When / Then
      testHelper
          .request(
              UID_1, new PermissionRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_3)))
          .andExpect(status().isOk())
          .andExpect(testHelper.isResponse(new PermissionRetrieveResponse(permissionPb(UID_3))));
    }
  }

  @Nested
  @DisplayName("update")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Update {

    private Stream<InvalidRequestCase<PermissionUpdateRequest>> invalidRequests() {
      return Stream.of(
          badRequest((PermissionUpdateRequest) updateRequest(UID_3).setName(null), "name is null"),
          badRequest(
              (PermissionUpdateRequest) updateRequest(UID_3).setName("ab"),
              "name below min length (3)"),
          badRequest(
              (PermissionUpdateRequest) updateRequest(UID_3).setName("a".repeat(41)),
              "name above max length (40)"),
          badRequest((PermissionUpdateRequest) updateRequest(UID_3).setCode(null), "code is null"),
          badRequest(
              (PermissionUpdateRequest) updateRequest(UID_3).setCode("ab"),
              "code below min length (3)"),
          badRequest(
              (PermissionUpdateRequest) updateRequest(UID_3).setCode("a".repeat(21)),
              "code above max length (20)"),
          badRequest((PermissionUpdateRequest) updateRequest(UID_3).setType(null), "type is null"),
          badRequest(
              (PermissionUpdateRequest) updateRequest(UID_3).setStatus(null), "status is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<PermissionUpdateRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_UPDATE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_PERMISSION_UPDATE permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(UID_1, updateRequest(UID_3))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_UPDATE permission -> response updated permission")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_UPDATE));
      testHelper.insertEntities(permission(UID_3));
      PermissionUpdateRequest request = updateRequest(UID_3);
      request.setName("updated-name");
      request.setDescription("updated-description");
      request.setType(PermissionType.UI);
      request.setStatus(PermissionStatus.INACTIVE);
      PermissionPb expected = permissionPb(UID_3);
      expected.setName("updated-name");
      expected.setDescription("updated-description");
      expected.setType(PermissionType.UI);
      expected.setStatus(PermissionStatus.INACTIVE);

      // When / Then
      testHelper
          .request(UID_1, request)
          .andExpect(status().isOk())
          .andExpect(testHelper.isResponse(new PermissionUpdateResponse(expected)));
    }
  }

  @Nested
  @DisplayName("updateStatus")
  class UpdateStatus {

    @Test
    @DisplayName("Given missing SYS_PERMISSION_STATUS permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(UID_1, new PermissionUpdateStatusRequest(UID_3, PermissionStatus.INACTIVE))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_STATUS permission -> response updated status")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_STATUS));
      testHelper.insertEntities(permission(UID_3));

      // When / Then
      testHelper
          .request(UID_1, new PermissionUpdateStatusRequest(UID_3, PermissionStatus.INACTIVE))
          .andExpect(status().isOk())
          .andExpect(
              testHelper.isResponse(
                  new PermissionUpdateStatusResponse(UID_3, PermissionStatus.INACTIVE)));
    }
  }

  @Nested
  @DisplayName("upload")
  class Upload {

    @Test
    @DisplayName("Given missing SYS_PERMISSION_UPLOAD permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(
              UID_1,
              new PermissionUploadRequest(java.util.List.of(editPb().setCode("upload-code"))))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_PERMISSION_UPLOAD permission -> response uploaded")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_PERMISSION_UPLOAD));
      when(uidGenerator.nextUid()).thenReturn(UID_3);

      // When / Then
      testHelper
          .request(
              UID_1,
              new PermissionUploadRequest(java.util.List.of(editPb().setCode("upload-code"))))
          .andExpect(status().isOk())
          .andExpect(testHelper.isResponse(new PermissionUploadResponse()));
    }
  }
}
