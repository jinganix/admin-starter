package io.github.jinganix.admin.starter.helper.exception;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("ApiException")
class ApiExceptionTest {

  @Nested
  @DisplayName("when creating exception")
  class WhenCreatingException {

    @Test
    @DisplayName("should return bad request exception when error code and message")
    void shouldReturnBadRequestExceptionWhenErrorCodeAndMessage() {
      ApiException exception = ApiException.of(ErrorCode.BAD_REQUEST, "invalid");

      assertThat(exception.getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
      assertThat(exception.getMessage()).isEqualTo("invalid");
      assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("should return exception when status and error code")
    void shouldReturnExceptionWhenStatusAndErrorCode() {
      ApiException exception = ApiException.of(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);

      assertThat(exception.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
      assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("should return bad request exception when error code only")
    void shouldReturnBadRequestExceptionWhenErrorCodeOnly() {
      ApiException exception = ApiException.of(ErrorCode.ERROR);

      assertThat(exception.getCode()).isEqualTo(ErrorCode.ERROR);
      assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  @DisplayName("should return same instance without stack trace when exception")
  void shouldReturnSameInstanceWithoutStackTraceWhenException() {
    ApiException exception = ApiException.of(ErrorCode.ERROR);

    assertThat(exception.fillInStackTrace()).isSameAs(exception);
  }
}
