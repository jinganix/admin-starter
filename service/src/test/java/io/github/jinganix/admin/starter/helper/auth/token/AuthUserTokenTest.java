package io.github.jinganix.admin.starter.helper.auth.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@DisplayName("AuthUserToken")
class AuthUserTokenTest {

  private static final Long USER_ID = 42L;

  private static final Set<GrantedAuthority> AUTHORITIES =
      Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

  @Test
  @DisplayName("should should expose user id as principal when token")
  void shouldShouldExposeUserIdAsPrincipalWhenToken() {
    AuthUserToken token = new AuthUserToken(USER_ID);
    token.setAuthorities(AUTHORITIES);

    assertThat(token.getPrincipal()).isEqualTo(USER_ID);
    assertThat(token.getUserId()).isEqualTo(USER_ID);
  }

  @Test
  @DisplayName("should should expose user id as credentials when token")
  void shouldShouldExposeUserIdAsCredentialsWhenToken() {
    AuthUserToken token = new AuthUserToken(USER_ID);

    assertThat(token.getCredentials()).isEqualTo(USER_ID);
  }

  @Test
  @DisplayName("should should expose user id as details when token")
  void shouldShouldExposeUserIdAsDetailsWhenToken() {
    AuthUserToken token = new AuthUserToken(USER_ID);

    assertThat(token.getDetails()).isEqualTo(USER_ID);
  }

  @Test
  @DisplayName("should should return configured authorities when token")
  void shouldShouldReturnConfiguredAuthoritiesWhenToken() {
    AuthUserToken token = new AuthUserToken(USER_ID);
    token.setAuthorities(AUTHORITIES);

    assertThat(token.getAuthorities())
        .singleElement()
        .isEqualTo(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Test
  @DisplayName("should should be authenticated by default when new token")
  void shouldShouldBeAuthenticatedByDefaultWhenNewToken() {
    AuthUserToken token = new AuthUserToken(USER_ID);

    assertThat(token.isAuthenticated()).isTrue();
  }

  @Test
  @DisplayName("should should reflect change when setAuthenticated false")
  void shouldShouldReflectChangeWhenSetAuthenticatedFalse() {
    AuthUserToken token = new AuthUserToken(USER_ID);

    token.setAuthenticated(false);

    assertThat(token.isAuthenticated()).isFalse();
  }

  @Test
  @DisplayName("should should return null name when token")
  void shouldShouldReturnNullNameWhenToken() {
    AuthUserToken token = new AuthUserToken(USER_ID);

    assertThat(token.getName()).isNull();
  }
}
