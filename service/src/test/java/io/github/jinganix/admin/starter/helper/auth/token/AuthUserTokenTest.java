package io.github.jinganix.admin.starter.helper.auth.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@DisplayName("AuthUserToken")
class AuthUserTokenTest {

  private static final Long USER_ID = 42L;

  private static final Set<GrantedAuthority> AUTHORITIES =
      Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

  @Nested
  @DisplayName("getPrincipal")
  class GetPrincipal {

    @Test
    @DisplayName("Given token -> should expose user id as principal")
    void givenTokenShouldExposeUserIdAsPrincipal() {
      AuthUserToken token = new AuthUserToken(USER_ID);
      token.setAuthorities(AUTHORITIES);

      assertThat(token.getPrincipal()).isEqualTo(USER_ID);
      assertThat(token.getUserId()).isEqualTo(USER_ID);
    }
  }

  @Nested
  @DisplayName("getCredentials")
  class GetCredentials {

    @Test
    @DisplayName("Given token -> should expose user id as credentials")
    void givenTokenShouldExposeUserIdAsCredentials() {
      AuthUserToken token = new AuthUserToken(USER_ID);

      assertThat(token.getCredentials()).isEqualTo(USER_ID);
    }
  }

  @Nested
  @DisplayName("getDetails")
  class GetDetails {

    @Test
    @DisplayName("Given token -> should expose user id as details")
    void givenTokenShouldExposeUserIdAsDetails() {
      AuthUserToken token = new AuthUserToken(USER_ID);

      assertThat(token.getDetails()).isEqualTo(USER_ID);
    }
  }

  @Nested
  @DisplayName("getAuthorities")
  class GetAuthorities {

    @Test
    @DisplayName("Given token -> should return configured authorities")
    void givenTokenShouldReturnConfiguredAuthorities() {
      AuthUserToken token = new AuthUserToken(USER_ID);
      token.setAuthorities(AUTHORITIES);

      assertThat(token.getAuthorities())
          .singleElement()
          .isEqualTo(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
  }

  @Nested
  @DisplayName("isAuthenticated")
  class IsAuthenticated {

    @Test
    @DisplayName("Given new token -> should be authenticated by default")
    void givenNewTokenShouldBeAuthenticatedByDefault() {
      AuthUserToken token = new AuthUserToken(USER_ID);

      assertThat(token.isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("Given setAuthenticated false -> should reflect change")
    void givenSetAuthenticatedFalseShouldReflectChange() {
      AuthUserToken token = new AuthUserToken(USER_ID);

      token.setAuthenticated(false);

      assertThat(token.isAuthenticated()).isFalse();
    }
  }

  @Nested
  @DisplayName("getName")
  class GetName {

    @Test
    @DisplayName("Given token -> should return null name")
    void givenTokenShouldReturnNullName() {
      AuthUserToken token = new AuthUserToken(USER_ID);

      assertThat(token.getName()).isNull();
    }
  }
}
