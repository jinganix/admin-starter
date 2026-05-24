package io.github.jinganix.admin.starter.helper.auth.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.auth.AuthorityService;
import io.github.jinganix.admin.starter.helper.auth.UserInactiveChecker;
import io.github.jinganix.admin.starter.tests.TestConst;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenAuthenticator")
class JwtTokenAuthenticatorTest {

  private TokenService tokenService;

  private UserInactiveChecker userInactiveChecker;

  private JwtTokenAuthenticator jwtTokenAuthenticator;

  @BeforeEach
  void setup() {
    tokenService = mock(TokenService.class);
    userInactiveChecker = mock(UserInactiveChecker.class);
    jwtTokenAuthenticator = new JwtTokenAuthenticator(tokenService, userInactiveChecker);
  }

  @Test
  @DisplayName("should return true when bearer token")
  void shouldReturnTrueWhenBearerToken() {
    assertThat(jwtTokenAuthenticator.support(new BearerTokenAuthenticationToken("token"))).isTrue();
  }

  @Test
  @DisplayName("should return false when other authentication")
  void shouldReturnFalseWhenOtherAuthentication() {
    assertThat(jwtTokenAuthenticator.support(new TestingAuthenticationToken("a", "b"))).isFalse();
  }

  @Nested
  @DisplayName("when authenticating bearer token")
  class WhenAuthenticatingBearerToken {

    @Test
    @DisplayName("should throws invalid bearer token when invalid token")
    void shouldThrowsInvalidBearerTokenWhenInvalidToken() {
      when(tokenService.decode("bad-token")).thenReturn(null);

      assertThatThrownBy(
              () ->
                  jwtTokenAuthenticator.authenticate(
                      new BearerTokenAuthenticationToken("bad-token")))
          .isInstanceOf(InvalidBearerTokenException.class);
    }

    @Test
    @DisplayName("should throws disabled exception when inactive user")
    void shouldThrowsDisabledExceptionWhenInactiveUser() {
      when(tokenService.decode("token")).thenReturn(new AuthUserToken(TestConst.UID_1));
      when(userInactiveChecker.isInactive(TestConst.UID_1)).thenReturn(true);

      assertThatThrownBy(
              () -> jwtTokenAuthenticator.authenticate(new BearerTokenAuthenticationToken("token")))
          .isInstanceOf(DisabledException.class);
    }

    @Test
    @DisplayName("should return token when active user without authority service")
    void shouldReturnTokenWhenActiveUserWithoutAuthorityService() {
      AuthUserToken token = new AuthUserToken(TestConst.UID_1);
      when(tokenService.decode("token")).thenReturn(token);
      when(userInactiveChecker.isInactive(TestConst.UID_1)).thenReturn(false);

      Authentication authenticated =
          jwtTokenAuthenticator.authenticate(new BearerTokenAuthenticationToken("token"));

      assertThat(authenticated).isInstanceOf(AuthUserToken.class);
      assertThat(((AuthUserToken) authenticated)).isSameAs(token);
      assertThat(authenticated.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("should attaches authorities when active user with authority service")
    void shouldAttachesAuthoritiesWhenActiveUserWithAuthorityService() {
      AuthUserToken token = new AuthUserToken(TestConst.UID_1);
      AuthorityService authorityService = mock(AuthorityService.class);
      when(tokenService.decode("token")).thenReturn(token);
      when(userInactiveChecker.isInactive(TestConst.UID_1)).thenReturn(false);
      when(authorityService.getApiAuthorities(TestConst.UID_1))
          .thenReturn(Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
      jwtTokenAuthenticator.setAuthorityService(authorityService);

      Authentication authenticated =
          jwtTokenAuthenticator.authenticate(new BearerTokenAuthenticationToken("token"));

      assertThat(authenticated).isInstanceOf(AuthUserToken.class);
      assertThat(((AuthUserToken) authenticated).getAuthorities())
          .singleElement()
          .isEqualTo(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
  }
}
