package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userDetailsPb;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserRetrieveHandler")
class UserRetrieveHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserRetrieveHandler userRetrieveHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowApiExceptionWhenUserNotFound() {
    // Given
    UserRetrieveRequest request = new UserRetrieveRequest().setId(UID_1);

    // When / Then
    assertThatThrownBy(() -> userRetrieveHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should return user details when existing user")
  void shouldReturnUserDetailsWhenExistingUser() {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));
    UserRetrieveRequest request = new UserRetrieveRequest().setId(UID_1);

    // When
    UserRetrieveResponse response = userRetrieveHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new UserRetrieveResponse()
                .setUser(userDetailsPb(UID_1, "user-10001", "foo", emptyList())));
  }
}
