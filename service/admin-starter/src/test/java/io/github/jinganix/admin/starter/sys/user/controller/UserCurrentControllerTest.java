package io.github.jinganix.admin.starter.sys.user.controller;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userCredential;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserController$current")
class UserCurrentControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when check fails")
  class WhenCheckFails {

    @Nested
    @DisplayName("when user not found")
    class WhenUserNotFound {

      @Test
      @DisplayName("then response error")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new UserCurrentRequest())
            .andExpect(testHelper.isError(ErrorCode.USER_NOT_FOUND));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Test
    @DisplayName("then response success")
    void thenResponseSuccess() throws Exception {
      testHelper.insertEntities(user(UID_1).setNickname("foo"), userCredential(UID_1));

      testHelper
          .request(UID_1, new UserCurrentRequest())
          .andExpect(status().isOk())
          .andExpect(
              testHelper.isResponse(
                  new UserCurrentResponse()
                      .setUser(
                          (UserCurrentPb)
                              new UserCurrentPb()
                                  .setAuthorities(List.of(RoleCode.AUTHED_USER.getCode()))
                                  .setId(UID_1)
                                  .setStatus(UserStatus.ACTIVE)
                                  .setNickname("foo")
                                  .setUsername("user-10001")
                                  .setCreatedAt(MIN_TIMESTAMP))));
    }
  }
}
