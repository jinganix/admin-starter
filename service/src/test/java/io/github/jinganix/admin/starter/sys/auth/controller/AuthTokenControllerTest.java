package io.github.jinganix.admin.starter.sys.auth.controller;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.auth.AuthData.userToken;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AuthController$token")
class AuthTokenControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when request is invalid")
  class WhenRequestIsInvalid {

    @Nested
    @DisplayName("when token is null")
    class WhenTokenIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new AuthTokenRequest())
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when token length < 1")
    class WhenTokenLengthLessThan1 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new AuthTokenRequest(""))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when token length > 40")
    class WhenTokenLengthGreater40 {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        String token = RandomStringUtils.insecure().nextAlphabetic(41);
        testHelper
            .request(UID_1, new AuthTokenRequest(token))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when check fails")
  class WhenCheckFails {

    @Nested
    @DisplayName("when token not found")
    class WhenTokenNotFound {

      @Test
      @DisplayName("then response UNAUTHORIZED")
      void thenResponseError() throws Exception {
        testHelper
            .request(UID_1, new AuthTokenRequest("abcd"))
            .andExpect(testHelper.isError(ErrorCode.BAD_REFRESH_TOKEN));
      }
    }

    @Nested
    @DisplayName("when user not found")
    class WhenUserNotFound {

      @Test
      @DisplayName("then response USER_NOT_FOUND")
      void thenResponseError() throws Exception {
        testHelper.insertEntities(userToken(UID_1).setRefreshToken("abc"));

        testHelper
            .request(UID_1, new AuthTokenRequest("abc"))
            .andExpect(testHelper.isError(ErrorCode.USER_NOT_FOUND));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Test
    @DisplayName("then response token")
    void thenResponseToken() throws Exception {
      testHelper.insertEntities(user(UID_1), userToken(UID_1).setRefreshToken("abc"));

      when(uidGenerator.nextUid()).thenReturn(UID_1);
      when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
      when(tokenService.generate(any())).thenReturn("test_token");

      testHelper
          .request(new AuthTokenRequest("abc"))
          .andExpect(status().isOk())
          .andExpect(
              testHelper.isResponse(
                  new AuthTokenResponse()
                      .setAccessToken("test_token")
                      .setExpiresIn(MILLIS + TimeUnit.MINUTES.toMillis(5))
                      .setRefreshToken("test_uuid")));
    }
  }
}
