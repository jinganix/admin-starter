package io.github.jinganix.admin.starter.sys.auth;

import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeExchangeCustomizer
    implements Customizer<
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

  @Override
  public void customize(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry) {
    allowPaths(
        registry,
        AuthLoginRequest.WEBPB_PATH,
        AuthSignupRequest.WEBPB_PATH,
        AuthTokenRequest.WEBPB_PATH);
    registry.anyRequest().authenticated();
  }

  private void allowPaths(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry,
      String... paths) {
    for (String path : paths) {
      String[] parts = path.split("\\?");
      registry.requestMatchers(parts[0]).permitAll();
    }
  }
}
