package io.github.jinganix.admin.starter.setup.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AuthenticationEntryPoint authenticationEntryPoint;

  private final Customizer<
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
      httpRequestsCustomizer;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityWebFilterChain(
      HttpSecurity security, AuthenticationManager authenticationManager) throws Exception {
    security.authenticationManager(authenticationManager);

    security.authorizeHttpRequests(httpRequestsCustomizer);

    BearerTokenAuthenticationFilter authenticationFilter =
        new BearerTokenAuthenticationFilter(authenticationManager);
    authenticationFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
    security.addFilter(authenticationFilter);

    security.exceptionHandling(spec -> spec.authenticationEntryPoint(authenticationEntryPoint));
    security.cors(spec -> spec.configurationSource(corsConfigurationSource()));
    security.csrf(AbstractHttpConfigurer::disable);
    return security.build();
  }

  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedHeader(CorsConfiguration.ALL);
    configuration.addAllowedMethod(CorsConfiguration.ALL);
    configuration.addAllowedOrigin(CorsConfiguration.ALL);
    configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
