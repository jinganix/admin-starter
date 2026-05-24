package io.github.jinganix.admin.starter.helper.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.service.error.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@DisplayName("DelegatedAuthenticationEntryPoint")
class DelegatedAuthenticationEntryPointTest {

  private DelegatedAuthenticationEntryPoint entryPoint;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    entryPoint = new DelegatedAuthenticationEntryPoint();
    objectMapper = new JsonMapper();
  }

  @Nested
  @DisplayName("when commencing unauthorized response")
  class WhenCommencingUnauthorizedResponse {

    @Test
    @DisplayName("should return BAD_TOKEN when invalid bearer token")
    void shouldReturnBadTokenWhenInvalidBearerToken() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(), response, new InvalidBearerTokenException("invalid"));

      assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.BAD_TOKEN);
    }

    @Test
    @DisplayName("should return BAD_CREDENTIAL when bad credentials")
    void shouldReturnBadCredentialWhenBadCredentials() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(), response, new BadCredentialsException("bad"));

      assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.BAD_CREDENTIAL);
    }

    @Test
    @DisplayName("should return USER_IS_INACTIVE when disabled user")
    void shouldReturnUserIsInactiveWhenDisabledUser() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(), response, new DisabledException("inactive"));

      assertThat(response.getStatus()).isEqualTo(FORBIDDEN.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.USER_IS_INACTIVE);
    }

    @Test
    @DisplayName("should return PERMISSION_DENIED when unknown authentication exception")
    void shouldReturnPermissionDeniedWhenUnknownAuthenticationException() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(),
          response,
          new org.springframework.security.core.AuthenticationException("unknown") {});

      assertThat(response.getStatus()).isEqualTo(FORBIDDEN.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.PERMISSION_DENIED);
    }
  }
}
