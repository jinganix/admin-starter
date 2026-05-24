package io.github.jinganix.admin.starter.helper.auth.token;

import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("TokenService")
class TokenServiceTest extends SpringBootIntegrationTests {

  private static final String JWT_SECRET = "test-jwt-secret";
  private static final String ISSUER = "io.github.jinganix.admin.starter";

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("generate")
  class Generate {

    @Test
    @DisplayName("Given user id -> return token that can be decoded")
    void givenUserId() {
      // Given
      Long userId = UID_1;

      // When
      String token = tokenService.generate(userId);
      AuthUserToken authUserToken = tokenService.decode(token);

      // Then
      assertThat(authUserToken).usingRecursiveComparison().isEqualTo(new AuthUserToken(userId));
    }
  }

  @Nested
  @DisplayName("decode")
  class Decode {

    @Test
    @DisplayName("Given token with invalid signature -> return null")
    void givenInvalidSignature() {
      // Given
      String text =
          JWT.create()
              .withClaim("uid", UID_1)
              .withIssuedAt(new Date(MILLIS))
              .withIssuer(ISSUER)
              .sign(Algorithm.HMAC256("wrong-secret"));

      // When
      AuthUserToken authUserToken = tokenService.decode(text);

      // Then
      assertThat(authUserToken).isNull();
    }

    @Test
    @DisplayName("Given expired token -> return null")
    void givenExpiredToken() {
      // Given
      String text =
          JWT.create()
              .withClaim("uid", UID_1)
              .withIssuedAt(new Date(MILLIS - TimeUnit.DAYS.toMillis(8)))
              .withIssuer(ISSUER)
              .sign(Algorithm.HMAC256(JWT_SECRET));

      // When
      AuthUserToken authUserToken = tokenService.decode(text);

      // Then
      assertThat(authUserToken).isNull();
    }

    @Test
    @DisplayName("Given null token text -> return null")
    void givenNullTokenText() {
      // Given
      String text = null;

      // When
      AuthUserToken authUserToken = tokenService.decode(text);

      // Then
      assertThat(authUserToken).isNull();
    }
  }
}
