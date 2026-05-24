package io.github.jinganix.admin.starter.sys.user;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userDetailsPb;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.InvalidRequestCase.badRequest;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.lib.pageable.SortDirection;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserDetailsPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserListRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserListResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.tests.InvalidRequestCase;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("UserController")
class UserControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return current user when existing user")
  void shouldReturnCurrentUserWhenExistingUser() throws Exception {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));

    // When / Then
    testHelper
        .request(UID_1, new UserCurrentRequest())
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                new UserCurrentResponse()
                    .setUser(
                        (UserCurrentPb)
                            new UserCurrentPb()
                                .setAuthorities(java.util.List.of(RoleCode.AUTHED_USER.getCode()))
                                .setId(UID_1)
                                .setStatus(UserStatus.ACTIVE)
                                .setNickname("foo")
                                .setUsername("user-10001")
                                .setCreatedAt(MIN_TIMESTAMP))));
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_USER_LIST permission")
  void shouldReturnAccessDeniedWhenMissingSysUserListPermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(
            UID_1,
            new UserListRequest(
                new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null, null))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return user list when SYS_USER_LIST permission")
  void shouldReturnUserListWhenSysUserListPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_LIST));
    testHelper.insertEntities(
        user(UID_1).setNickname("foo"),
        user(UID_2).setNickname("bar"),
        userIdentity(UID_1),
        userIdentity(UID_2));
    UserPb user1 =
        new UserPb()
            .setId(UID_1)
            .setUsername("user-10001")
            .setNickname("foo")
            .setStatus(UserStatus.ACTIVE)
            .setCreatedAt(MIN_TIMESTAMP);
    UserPb user2 =
        new UserPb()
            .setId(UID_2)
            .setUsername("user-10002")
            .setNickname("bar")
            .setStatus(UserStatus.ACTIVE)
            .setCreatedAt(MIN_TIMESTAMP);

    // When / Then
    testHelper
        .request(UID_1, new UserListRequest(new PageablePb(), null, null, null))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                testHelper
                    .paging(2, new UserListResponse(java.util.List.of(user1, user2)))
                    .setPages(1)));
  }

  @Nested
  @DisplayName("when change password request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenChangePasswordRequestIsInvalid {

    private Stream<InvalidRequestCase<UserChangePasswordRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              new UserChangePasswordRequest(null, null),
              "should return bad request when current and password are null"),
          badRequest(
              new UserChangePasswordRequest("12345", "123456"),
              "should return bad request when current below min length (6)"),
          badRequest(
              new UserChangePasswordRequest("123456789012345678901", "123456"),
              "should return bad request when current above max length (20)"),
          badRequest(
              new UserChangePasswordRequest("123456", null),
              "should return bad request when password is null"),
          badRequest(
              new UserChangePasswordRequest("123456", "12345"),
              "should return bad request when password below min length (6)"),
          badRequest(
              new UserChangePasswordRequest("123456", "123456789012345678901"),
              "should return bad request when password above max length (20)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(
        InvalidRequestCase<UserChangePasswordRequest> testCase) throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing AUTHED_USER role")
  void shouldReturnAccessDeniedWhenMissingAuthedUserRoleForChangePassword() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());

    // When / Then
    testHelper
        .request(UID_1, new UserChangePasswordRequest("123456", "654321"))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return ok when valid request")
  void shouldReturnOkWhenValidChangePasswordRequest() throws Exception {
    // Given
    String password = "password123";
    testHelper.insertEntities(
        user(UID_1).setNickname("foo"),
        userIdentity(UID_1).setPassword(passwordEncoder.encode(password)));

    // When / Then
    testHelper
        .request(UID_1, new UserChangePasswordRequest(password, "newpassword"))
        .andExpect(status().isOk());
  }

  @Nested
  @DisplayName("when create request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenCreateRequestIsInvalid {

    private Stream<InvalidRequestCase<UserCreateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              createRequest(null, null, null),
              "should return bad request when username, password and status are null"),
          badRequest(
              createRequest("ab", "123456", UserStatus.ACTIVE),
              "should return bad request when username below min length (3)"),
          badRequest(
              createRequest("abcdefghijklmnopqrstu", "123456", UserStatus.ACTIVE),
              "should return bad request when username above max length (20)"),
          badRequest(
              createRequest("aaaaaa", null, UserStatus.ACTIVE),
              "should return bad request when password is null"),
          badRequest(
              createRequest("aaaaaa", "12", UserStatus.ACTIVE),
              "should return bad request when password below min length (3)"),
          badRequest(
              createRequest("aaaaaa", "123456789012345678901", UserStatus.ACTIVE),
              "should return bad request when password above max length (20)"),
          badRequest(
              createRequest("aaaaaa", "aaaaaa", null),
              "should return bad request when status is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<UserCreateRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_CREATE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_USER_CREATE permission")
  void shouldReturnAccessDeniedWhenMissingSysUserCreatePermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(
            UID_1,
            createRequest("newuser", "password", UserStatus.ACTIVE)
                .setRoleIds(Collections.emptyList()))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return ok when SYS_USER_CREATE permission")
  void shouldReturnOkWhenSysUserCreatePermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_CREATE));
    when(uidGenerator.nextUid()).thenReturn(UID_3, UID_3 + 1);

    // When / Then
    testHelper
        .request(
            UID_1,
            createRequest("newuser", "password", UserStatus.ACTIVE)
                .setRoleIds(Collections.emptyList()))
        .andExpect(status().isOk());
  }

  @Nested
  @DisplayName("when delete request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenDeleteRequestIsInvalid {

    private Stream<InvalidRequestCase<UserDeleteRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new UserDeleteRequest(), "should return bad request when ids is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<UserDeleteRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_DELETE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_USER_DELETE permission")
  void shouldReturnAccessDeniedWhenMissingSysUserDeletePermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

    // When / Then
    testHelper
        .request(UID_1, new UserDeleteRequest(java.util.List.of(UID_2)))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return ok when SYS_USER_DELETE permission")
  void shouldReturnOkWhenSysUserDeletePermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_DELETE));
    testHelper.insertEntities(user(UID_3).setNickname("bar"), userIdentity(UID_3));

    // When / Then
    testHelper
        .request(
            UID_1, (UserDeleteRequest) new UserDeleteRequest().setIds(java.util.List.of(UID_3)))
        .andExpect(status().isOk());
  }

  @Nested
  @DisplayName("when retrieve request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenRetrieveRequestIsInvalid {

    private Stream<InvalidRequestCase<UserRetrieveRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new UserRetrieveRequest(), "should return bad request when id is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(
        InvalidRequestCase<UserRetrieveRequest> testCase) throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_LIST));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_USER_LIST permission")
  void shouldReturnAccessDeniedWhenMissingSysUserListPermissionForRetrieve() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

    // When / Then
    testHelper
        .request(UID_1, new UserRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_3)))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return user details when SYS_USER_LIST permission")
  void shouldReturnUserDetailsWhenSysUserListPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_LIST));
    testHelper.insertEntities(user(UID_3).setNickname("bar"), userIdentity(UID_3));

    // When / Then
    testHelper
        .request(UID_1, new UserRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_3)))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                new UserRetrieveResponse()
                    .setUser(userDetailsPb(UID_3, "user-10003", "bar", Collections.emptyList()))));
  }

  @Nested
  @DisplayName("when update request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenUpdateRequestIsInvalid {

    private Stream<InvalidRequestCase<UserUpdateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              updateRequest(UID_2, null, UserStatus.ACTIVE),
              "should return bad request when nickname is null"),
          badRequest(
              updateRequest(UID_2, "ab", UserStatus.ACTIVE),
              "should return bad request when nickname below min length (3)"),
          badRequest(
              updateRequest(UID_2, "abcdefghijklmnopqrstu", UserStatus.ACTIVE),
              "should return bad request when nickname above max length (20)"),
          badRequest(
              updateRequest(UID_2, "aaaaaa", null),
              "should return bad request when status is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<UserUpdateRequest> testCase)
        throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_UPDATE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_USER_UPDATE permission")
  void shouldReturnAccessDeniedWhenMissingSysUserUpdatePermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

    // When / Then
    testHelper
        .request(
            UID_1,
            updateRequest(UID_2, "aaaaaa", UserStatus.ACTIVE).setRoleIds(Collections.emptyList()))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return updated user when SYS_USER_UPDATE permission")
  void shouldReturnUpdatedUserWhenSysUserUpdatePermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_UPDATE));
    testHelper.insertEntities(user(UID_3).setNickname("bar"), userIdentity(UID_3));

    // When / Then
    testHelper
        .request(
            UID_1,
            updateRequest(UID_3, "updated", UserStatus.INACTIVE)
                .setRoleIds(Collections.emptyList()))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(new UserUpdateResponse().setUser(expectedUpdatedUser(UID_3))));
  }

  @Nested
  @DisplayName("when update profile request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenUpdateProfileRequestIsInvalid {

    private Stream<InvalidRequestCase<UserUpdateProfileRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              new UserUpdateProfileRequest(null),
              "should return bad request when nickname is null"),
          badRequest(
              new UserUpdateProfileRequest("ab"),
              "should return bad request when nickname below min length (3)"),
          badRequest(
              new UserUpdateProfileRequest("abcdefghijklmnopqrstu"),
              "should return bad request when nickname above max length (20)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(
        InvalidRequestCase<UserUpdateProfileRequest> testCase) throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing AUTHED_USER role")
  void shouldReturnAccessDeniedWhenMissingAuthedUserRoleForUpdateProfile() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());

    // When / Then
    testHelper
        .request(UID_1, new UserUpdateProfileRequest("updated"))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return updated user when valid request")
  void shouldReturnUpdatedUserWhenValidUpdateProfileRequest() throws Exception {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));

    // When / Then
    testHelper
        .request(UID_1, new UserUpdateProfileRequest("updated"))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                new UserUpdateProfileResponse(
                    new UserPb()
                        .setId(UID_1)
                        .setUsername("user-10001")
                        .setNickname("updated")
                        .setStatus(UserStatus.ACTIVE)
                        .setCreatedAt(MIN_TIMESTAMP))));
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_USER_STATUS permission")
  void shouldReturnAccessDeniedWhenMissingSysUserStatusPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

    // When / Then
    testHelper
        .request(UID_1, new UserUpdateStatusRequest(UID_2, UserStatus.INACTIVE))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return updated user when SYS_USER_STATUS permission")
  void shouldReturnUpdatedUserWhenSysUserStatusPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_STATUS));
    testHelper.insertEntities(user(UID_3).setNickname("bar"), userIdentity(UID_3));

    // When / Then
    testHelper
        .request(UID_1, new UserUpdateStatusRequest(UID_3, UserStatus.INACTIVE))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                new UserUpdateStatusResponse(
                    (UserPb)
                        new UserPb()
                            .setId(UID_3)
                            .setUsername("user-10003")
                            .setNickname("bar")
                            .setStatus(UserStatus.INACTIVE)
                            .setCreatedAt(MIN_TIMESTAMP))));
  }

  private static UserCreateRequest createRequest(
      String username, String password, UserStatus status) {
    UserCreateRequest request = new UserCreateRequest();
    request.setUsername(username);
    request.setPassword(password);
    request.setStatus(status);
    return request;
  }

  private static UserUpdateRequest updateRequest(Long id, String nickname, UserStatus status) {
    UserUpdateRequest request = new UserUpdateRequest();
    request.setId(id);
    request.setNickname(nickname);
    request.setStatus(status);
    return request;
  }

  private static UserDetailsPb expectedUpdatedUser(long id) {
    UserDetailsPb user = userDetailsPb(id, "user-" + id, "updated", Collections.emptyList());
    user.setStatus(UserStatus.INACTIVE);
    return user;
  }
}
