package io.github.jinganix.admin.starter.helper.auth.security;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.service.error.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    ApiException ex =
        switch (exception) {
          case InvalidBearerTokenException _e -> ApiException.of(UNAUTHORIZED, ErrorCode.BAD_TOKEN);
          case BadCredentialsException _e ->
              ApiException.of(UNAUTHORIZED, ErrorCode.BAD_CREDENTIAL);
          case DisabledException _e -> ApiException.of(FORBIDDEN, ErrorCode.USER_IS_INACTIVE);
          default -> ApiException.of(FORBIDDEN, ErrorCode.PERMISSION_DENIED);
        };

    response.setStatus(ex.getStatus().value());
    ErrorMessage message = new ErrorMessage(ex.getCode(), ex.getMessage(), null);
    response.getWriter().write(objectMapper.writeValueAsString(message));
  }
}
