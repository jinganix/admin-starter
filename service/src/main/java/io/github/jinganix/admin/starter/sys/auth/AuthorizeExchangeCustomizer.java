package io.github.jinganix.admin.starter.sys.auth;

import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.webpb.runtime.WebpbMeta;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeExchangeCustomizer
    implements Customizer<
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

  @Override
  public void customize(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry) {
    permitAll(registry, AuthLoginRequest.WEBPB_META);
    permitAll(registry, AuthSignupRequest.WEBPB_META);
    permitAll(registry, AuthTokenRequest.WEBPB_META);
    registry.requestMatchers(AnyRequestMatcher.INSTANCE).authenticated();
  }

  private void permitAll(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry,
      WebpbMeta meta) {
    this.permitAll(registry, meta.getMethod(), meta.getPath());
  }

  private void permitAll(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry,
      String method,
      String path) {
    String[] parts = path.split("\\?");
    registry.requestMatchers(HttpMethod.valueOf(method), parts[0]).permitAll();
  }
}
