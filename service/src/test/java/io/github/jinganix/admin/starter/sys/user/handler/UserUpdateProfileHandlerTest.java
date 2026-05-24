package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserUpdateProfileHandler")
class UserUpdateProfileHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserUpdateProfileHandler userUpdateProfileHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given user not found -> throw ApiException")
  void givenUserNotFound() {
    // Given
    UserUpdateProfileRequest request = new UserUpdateProfileRequest().setNickname("updated");

    // When / Then
    assertThatThrownBy(() -> userUpdateProfileHandler.handle(UID_1, request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("Given existing user -> return updated profile")
  void givenExistingUser() {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));
    UserUpdateProfileRequest request = new UserUpdateProfileRequest().setNickname("updated");

    // When
    UserUpdateProfileResponse response = userUpdateProfileHandler.handle(UID_1, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new UserUpdateProfileResponse()
                .setUser(
                    new UserPb()
                        .setId(UID_1)
                        .setUsername("user-10001")
                        .setNickname("updated")
                        .setStatus(UserStatus.ACTIVE)
                        .setCreatedAt(MIN_TIMESTAMP)));
  }
}
