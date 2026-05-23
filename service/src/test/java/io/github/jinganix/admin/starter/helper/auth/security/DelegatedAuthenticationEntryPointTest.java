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
  @DisplayName("commence")
  class Commence {

    @Test
    @DisplayName("Given invalid bearer token -> response BAD_TOKEN")
    void givenInvalidBearerToken() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(), response, new InvalidBearerTokenException("invalid"));

      assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.BAD_TOKEN);
    }

    @Test
    @DisplayName("Given bad credentials -> response BAD_CREDENTIAL")
    void givenBadCredentials() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(), response, new BadCredentialsException("bad"));

      assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.BAD_CREDENTIAL);
    }

    @Test
    @DisplayName("Given disabled user -> response USER_IS_INACTIVE")
    void givenDisabledUser() throws Exception {
      MockHttpServletResponse response = new MockHttpServletResponse();

      entryPoint.commence(
          new MockHttpServletRequest(), response, new DisabledException("inactive"));

      assertThat(response.getStatus()).isEqualTo(FORBIDDEN.value());
      ErrorMessage message =
          objectMapper.readValue(response.getContentAsString(), ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(ErrorCode.USER_IS_INACTIVE);
    }

    @Test
    @DisplayName("Given unknown authentication exception -> response PERMISSION_DENIED")
    void givenUnknownAuthenticationException() throws Exception {
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
