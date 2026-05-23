package io.github.jinganix.admin.starter.setup.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.service.error.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;

@DisplayName("GlobalExceptionHandlerUnit")
class GlobalExceptionHandlerUnitTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setup() {
    handler = new GlobalExceptionHandler();
  }

  @Nested
  @DisplayName("handleValidationException")
  class HandleValidationException {

    @Test
    @DisplayName("Given field errors -> returns bad request with field map")
    void givenFieldErrors() {
      BeanPropertyBindingResult bindingResult =
          new BeanPropertyBindingResult(new Object(), "target");
      bindingResult.addError(new FieldError("target", "username", "must not be blank"));
      MethodArgumentNotValidException exception =
          new MethodArgumentNotValidException(null, bindingResult);

      ResponseEntity<?> response = handler.handleValidationException(exception);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(response.getBody()).isInstanceOf(ErrorMessage.class);
      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
      assertThat(body.getErrors()).containsEntry("username", "must not be blank");
    }
  }

  @Nested
  @DisplayName("handleBindException")
  class HandleBindException {

    @Test
    @DisplayName("Given web exchange bind exception -> returns bad request")
    void givenWebExchangeBindException() {
      WebExchangeBindException exception = mock(WebExchangeBindException.class);
      when(exception.getMessage()).thenReturn("bind failed");

      ResponseEntity<?> response = handler.handleBindException(exception);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
      assertThat(body.getMessage()).isEqualTo("bind failed");
    }
  }

  @Nested
  @DisplayName("handleApiException")
  class HandleApiException {

    @Test
    @DisplayName("Given api exception -> returns configured status and code")
    void givenApiException() {
      ApiException exception = ApiException.of(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);

      ResponseEntity<?> response = handler.handleApiException(exception);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("handleAuthenticationException")
  class HandleAuthenticationException {

    @Test
    @DisplayName("Given disabled exception -> returns user inactive")
    void givenDisabledException() {
      ResponseEntity<?> response =
          handler.handleAuthenticationException(new DisabledException("inactive"));

      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.USER_IS_INACTIVE);
    }

    @Test
    @DisplayName("Given bad credentials -> returns bad credential")
    void givenBadCredentials() {
      ResponseEntity<?> response =
          handler.handleAuthenticationException(new BadCredentialsException("bad"));

      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.BAD_CREDENTIAL);
    }
  }

  @Nested
  @DisplayName("handleAccessDeniedException")
  class HandleAccessDeniedException {

    @Test
    @DisplayName("Given access denied -> returns forbidden")
    void givenAccessDenied() {
      ResponseEntity<?> response =
          handler.handleAccessDeniedException(new AccessDeniedException("denied"));

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
    }
  }

  @Nested
  @DisplayName("handleGenericException")
  class HandleGenericException {

    @Test
    @DisplayName("Given error response exception -> returns configured status")
    void givenErrorResponseException() {
      Exception errorResponse = new ErrorResponseException("conflict", HttpStatus.CONFLICT);

      ResponseEntity<?> response = handler.handleGenericException(errorResponse);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.ERROR);
      assertThat(body.getMessage()).isEqualTo("conflict");
    }

    @Test
    @DisplayName("Given generic exception -> returns internal server error")
    void givenGenericException() {
      ResponseEntity<?> response = handler.handleGenericException(new RuntimeException("boom"));

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      ErrorMessage body = (ErrorMessage) response.getBody();
      assertThat(body.getCode()).isEqualTo(ErrorCode.ERROR);
      assertThat(body.getMessage()).isEqualTo("boom");
    }
  }

  private static final class ErrorResponseException extends Exception implements ErrorResponse {

    private final HttpStatusCode status;

    private ErrorResponseException(String message, HttpStatus status) {
      super(message);
      this.status = status;
    }

    @Override
    public HttpStatusCode getStatusCode() {
      return status;
    }

    @Override
    public org.springframework.http.ProblemDetail getBody() {
      return org.springframework.http.ProblemDetail.forStatus(status);
    }
  }
}
