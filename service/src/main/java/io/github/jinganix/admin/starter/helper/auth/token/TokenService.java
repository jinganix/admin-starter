package io.github.jinganix.admin.starter.helper.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

  private static final String USER_ID = "uid";

  private Algorithm algorithm;

  private JWTVerifier verifier;

  @Value("${config.jwt-secret}")
  private final String jwtSecret;

  @Value("${config.issuer}")
  private final String issuer;

  private final UtilsService utilsService;

  @PostConstruct
  void initialize() {
    this.algorithm = Algorithm.HMAC256(jwtSecret);
    this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
  }

  public String generate(Long userId) {
    return JWT.create()
        .withClaim(USER_ID, userId)
        .withIssuedAt(new Date())
        .withIssuer(issuer)
        .sign(algorithm);
  }

  public AuthUserToken decode(String text) {
    try {
      DecodedJWT jwt = verifier.verify(text);
      long millis = utilsService.currentTimeMillis();
      if (jwt.getIssuedAt().getTime() < millis - TimeUnit.DAYS.toMillis(7)) {
        return null;
      }
      Map<String, Claim> claims = jwt.getClaims();
      return new AuthUserToken(claims.get(USER_ID).asLong());
    } catch (Exception e) {
      log.error("token: " + text + " error: " + e.getMessage());
      return null;
    }
  }
}
