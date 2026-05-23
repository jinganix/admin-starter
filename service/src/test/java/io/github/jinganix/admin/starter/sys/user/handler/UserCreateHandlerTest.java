package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserCreateHandler")
class UserCreateHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserCreateHandler userCreateHandler;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired UserRepository userRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given existing username -> throw ApiException")
  void givenExistingUsername() {
    // Given
    testHelper.insertEntities(user(UID_1), userIdentity(UID_1).setUsername("existing-user"));
    UserCreateRequest request =
        (UserCreateRequest)
            new UserCreateRequest()
                .setUsername("existing-user")
                .setPassword("password123")
                .setStatus(UserStatus.ACTIVE)
                .setRoleIds(Collections.emptyList());

    // When / Then
    assertThatThrownBy(() -> userCreateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USERNAME_EXISTS));
  }

  @Test
  @DisplayName("Given valid request -> create user")
  void givenValidRequest() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2);
    UserCreateRequest request =
        (UserCreateRequest)
            new UserCreateRequest()
                .setUsername("new-user")
                .setPassword("password123")
                .setStatus(UserStatus.ACTIVE)
                .setRoleIds(Collections.emptyList());

    // When
    UserCreateResponse response = userCreateHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new UserCreateResponse());
    assertThat(adminUserIdentityRepository.findByUsername("new-user")).isNotNull();
    User user = userRepository.findById(UID_1);
    assertThat(user.getNickname()).isEqualTo("new-user");
    assertThat(user.getCreatedAt()).isEqualTo(MILLIS);
  }
}
