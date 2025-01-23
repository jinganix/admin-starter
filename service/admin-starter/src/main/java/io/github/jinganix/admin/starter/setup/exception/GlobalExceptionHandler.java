package io.github.jinganix.admin.starter.setup.exception;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.service.error.ErrorMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/** Global exception handler. */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  /**
   * Format validation exception error message.
   *
   * @param ex {@link MethodArgumentNotValidException}
   * @return map of messages
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessage(ErrorCode.BAD_REQUEST, null, errors));
  }

  /**
   * Format validation exception error message.
   *
   * @param ex {@link WebExchangeBindException}
   * @return map of messages
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<?> handleBindException(WebExchangeBindException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessage(ErrorCode.BAD_REQUEST, ex.getMessage(), null));
  }

  /**
   * Handle api exception.
   *
   * @param ex {@link ApiException}
   * @return {@link ErrorMessage}
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<?> handleApiException(ApiException ex) {
    return ResponseEntity.status(ex.getStatus())
        .body(new ErrorMessage().setCode(ex.getCode()).setMessage(ex.getMessage()));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorMessage(ErrorCode.BAD_CREDENTIAL, ex.getMessage(), null));
  }

  /**
   * Handle access denied exception.
   *
   * @param ex {@link AccessDeniedException}
   * @return {@link ErrorMessage}
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorMessage(ErrorCode.ACCESS_DENIED, ex.getMessage(), null));
  }

  /**
   * Handle generic exception.
   *
   * @param ex {@link Exception}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler
  public ResponseEntity<?> handleGenericException(Exception ex) {
    if (ex instanceof ErrorResponse error) {
      return ResponseEntity.status(error.getStatusCode())
          .body(new ErrorMessage(ErrorCode.ERROR, ex.getMessage(), null));
    }
    log.error("Generic exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorMessage(ErrorCode.ERROR, ex.getMessage(), null));
  }
}
