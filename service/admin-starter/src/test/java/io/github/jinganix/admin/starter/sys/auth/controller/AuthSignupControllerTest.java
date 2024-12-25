package io.github.jinganix.admin.starter.sys.auth.controller;

import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
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

@DisplayName("AuthController$signup")
class AuthSignupControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when request is invalid")
  class WhenRequestIsInvalid {

    @Nested
    @DisplayName("when username is null")
    class WhenUsernameIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(new AuthSignupRequest(null, null))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when username is too short")
    class WhenUsernameIsTooShort {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(new AuthSignupRequest("", "aaaaaa"))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when username is too long")
    class WhenUsernameIsTooLong {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        String username = RandomStringUtils.insecure().nextAlphabetic(21);
        testHelper
            .request(new AuthSignupRequest(username, "aaaaaa"))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when password is null")
    class WhenPasswordIsNull {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(new AuthSignupRequest("aaaaaa", null))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when password is too short")
    class WhenPasswordIsTooShort {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        testHelper
            .request(new AuthSignupRequest("aaaaaa", ""))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }

    @Nested
    @DisplayName("when password is too long")
    class WhenPasswordIsTooLong {

      @Test
      @DisplayName("then response BAD_REQUEST")
      void thenResponseError() throws Exception {
        String password = RandomStringUtils.insecure().nextAlphabetic(21);
        testHelper
            .request(new AuthSignupRequest("aaaaaa", password))
            .andExpect(status().isBadRequest())
            .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @Nested
    @DisplayName("when auth success")
    class WhenAuthSuccess {

      @Test
      @DisplayName("then response token")
      void thenResponseToken() throws Exception {
        when(uidGenerator.nextUid()).thenReturn(UID_1);
        when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
        when(tokenService.generate(any())).thenReturn("test_token");
        doReturn(new AuthUserToken(UID_1)).when(credentialsAuthenticator).authenticate(any());

        testHelper
            .request(new AuthSignupRequest("aaaaaa", "aaaaaa"))
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
}
