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

  @Nested
  @DisplayName("current")
  class Current {

    @Test
    @DisplayName("Given existing user -> response current user")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("list")
  class List {

    @Test
    @DisplayName("Given missing SYS_USER_LIST permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(
              UID_1,
              new UserListRequest(
                  new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null, null))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_USER_LIST permission -> response user list")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("changePassword")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class ChangePassword {

    private Stream<InvalidRequestCase<UserChangePasswordRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new UserChangePasswordRequest(null, null), "current and password are null"),
          badRequest(
              new UserChangePasswordRequest("12345", "123456"), "current below min length (6)"),
          badRequest(
              new UserChangePasswordRequest("123456789012345678901", "123456"),
              "current above max length (20)"),
          badRequest(new UserChangePasswordRequest("123456", null), "password is null"),
          badRequest(
              new UserChangePasswordRequest("123456", "12345"), "password below min length (6)"),
          badRequest(
              new UserChangePasswordRequest("123456", "123456789012345678901"),
              "password above max length (20)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<UserChangePasswordRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing AUTHED_USER role -> response ACCESS_DENIED")
    void givenMissingRole() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());

      // When / Then
      testHelper
          .request(UID_1, new UserChangePasswordRequest("123456", "654321"))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given valid request -> response ok")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("create")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Create {

    private Stream<InvalidRequestCase<UserCreateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(createRequest(null, null, null), "username, password and status are null"),
          badRequest(
              createRequest("ab", "123456", UserStatus.ACTIVE), "username below min length (3)"),
          badRequest(
              createRequest("abcdefghijklmnopqrstu", "123456", UserStatus.ACTIVE),
              "username above max length (20)"),
          badRequest(createRequest("aaaaaa", null, UserStatus.ACTIVE), "password is null"),
          badRequest(
              createRequest("aaaaaa", "12", UserStatus.ACTIVE), "password below min length (3)"),
          badRequest(
              createRequest("aaaaaa", "123456789012345678901", UserStatus.ACTIVE),
              "password above max length (20)"),
          badRequest(createRequest("aaaaaa", "aaaaaa", null), "status is null"));
    }

    private UserCreateRequest createRequest(String username, String password, UserStatus status) {
      UserCreateRequest request = new UserCreateRequest();
      request.setUsername(username);
      request.setPassword(password);
      request.setStatus(status);
      return request;
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<UserCreateRequest> testCase) throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_CREATE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_USER_CREATE permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given / When / Then
      testHelper
          .request(
              UID_1,
              createRequest("newuser", "password", UserStatus.ACTIVE)
                  .setRoleIds(Collections.emptyList()))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_USER_CREATE permission -> response ok")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("delete")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Delete {

    private Stream<InvalidRequestCase<UserDeleteRequest>> invalidRequests() {
      return Stream.of(badRequest(new UserDeleteRequest(), "ids is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<UserDeleteRequest> testCase) throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_DELETE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_USER_DELETE permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

      // When / Then
      testHelper
          .request(UID_1, new UserDeleteRequest(java.util.List.of(UID_2)))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_USER_DELETE permission -> response ok")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("retrieve")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Retrieve {

    private Stream<InvalidRequestCase<UserRetrieveRequest>> invalidRequests() {
      return Stream.of(badRequest(new UserRetrieveRequest(), "id is null"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<UserRetrieveRequest> testCase) throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_LIST));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_USER_LIST permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

      // When / Then
      testHelper
          .request(UID_1, new UserRetrieveRequest(), java.util.Map.of("id", String.valueOf(UID_3)))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_USER_LIST permission -> response user details")
    void givenValidRequest() throws Exception {
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
                      .setUser(
                          userDetailsPb(UID_3, "user-10003", "bar", Collections.emptyList()))));
    }
  }

  @Nested
  @DisplayName("update")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Update {

    private Stream<InvalidRequestCase<UserUpdateRequest>> invalidRequests() {
      return Stream.of(
          badRequest(updateRequest(UID_2, null, UserStatus.ACTIVE), "nickname is null"),
          badRequest(
              updateRequest(UID_2, "ab", UserStatus.ACTIVE), "nickname below min length (3)"),
          badRequest(
              updateRequest(UID_2, "abcdefghijklmnopqrstu", UserStatus.ACTIVE),
              "nickname above max length (20)"),
          badRequest(updateRequest(UID_2, "aaaaaa", null), "status is null"));
    }

    private UserUpdateRequest updateRequest(Long id, String nickname, UserStatus status) {
      UserUpdateRequest request = new UserUpdateRequest();
      request.setId(id);
      request.setNickname(nickname);
      request.setStatus(status);
      return request;
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<UserUpdateRequest> testCase) throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_UPDATE));

      // When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing SYS_USER_UPDATE permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
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
    @DisplayName("Given SYS_USER_UPDATE permission -> response updated user")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("updateProfile")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class UpdateProfile {

    private Stream<InvalidRequestCase<UserUpdateProfileRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new UserUpdateProfileRequest(null), "nickname is null"),
          badRequest(new UserUpdateProfileRequest("ab"), "nickname below min length (3)"),
          badRequest(
              new UserUpdateProfileRequest("abcdefghijklmnopqrstu"),
              "nickname above max length (20)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void givenInvalidRequest(InvalidRequestCase<UserUpdateProfileRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }

    @Test
    @DisplayName("Given missing AUTHED_USER role -> response ACCESS_DENIED")
    void givenMissingRole() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());

      // When / Then
      testHelper
          .request(UID_1, new UserUpdateProfileRequest("updated"))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given valid request -> response updated user")
    void givenValidRequest() throws Exception {
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
  }

  @Nested
  @DisplayName("updateStatus")
  class UpdateStatus {

    @Test
    @DisplayName("Given missing SYS_USER_STATUS permission -> response ACCESS_DENIED")
    void givenMissingPermission() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(java.util.Set.of());

      // When / Then
      testHelper
          .request(UID_1, new UserUpdateStatusRequest(UID_2, UserStatus.INACTIVE))
          .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("Given SYS_USER_STATUS permission -> response updated user")
    void givenValidRequest() throws Exception {
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
  }

  private static UserDetailsPb expectedUpdatedUser(long id) {
    UserDetailsPb user = userDetailsPb(id, "user-" + id, "updated", Collections.emptyList());
    user.setStatus(UserStatus.INACTIVE);
    return user;
  }
}
