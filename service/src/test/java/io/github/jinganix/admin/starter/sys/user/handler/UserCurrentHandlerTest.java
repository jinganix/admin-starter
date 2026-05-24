package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserCurrentHandler")
class UserCurrentHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserCurrentHandler userCurrentHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowApiExceptionWhenUserNotFound() {
    // Given
    Long userId = UID_1;

    // When / Then
    assertThatThrownBy(() -> userCurrentHandler.handle(userId))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should return current user response when existing user")
  void shouldReturnCurrentUserResponseWhenExistingUser() {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));

    // When
    UserCurrentResponse response = userCurrentHandler.handle(UID_1);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new UserCurrentResponse()
                .setUser(
                    (UserCurrentPb)
                        new UserCurrentPb()
                            .setAuthorities(List.of(RoleCode.AUTHED_USER.getCode()))
                            .setId(UID_1)
                            .setStatus(UserStatus.ACTIVE)
                            .setNickname("foo")
                            .setUsername("user-10001")
                            .setCreatedAt(MIN_TIMESTAMP)));
  }
}
