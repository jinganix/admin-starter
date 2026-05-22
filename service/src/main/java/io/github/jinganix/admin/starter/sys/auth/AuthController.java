package io.github.jinganix.admin.starter.sys.auth;

import io.github.jinganix.admin.starter.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.handler.AuthLoginHandler;
import io.github.jinganix.admin.starter.sys.auth.handler.AuthSignupHandler;
import io.github.jinganix.admin.starter.sys.auth.handler.AuthTokenHandler;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthLoginHandler authLoginHandler;

  private final AuthSignupHandler authSignupHandler;

  private final AuthTokenHandler authTokenHandler;

  private final OrderedTraceExecutor orderedTraceExecutor;

  @WebpbRequestMapping
  public AuthTokenResponse login(@Valid @RequestBody AuthLoginRequest request) {
    return authLoginHandler.handle(request);
  }

  @WebpbRequestMapping
  public AuthTokenResponse signup(@Valid @RequestBody AuthSignupRequest request) {
    return orderedTraceExecutor.supply("USER_CREATE", () -> authSignupHandler.handle(request));
  }

  @WebpbRequestMapping
  public AuthTokenResponse token(@Valid @RequestBody AuthTokenRequest request) {
    return orderedTraceExecutor.supply(
        request.getRefreshToken(), () -> authTokenHandler.handle(request));
  }
}
