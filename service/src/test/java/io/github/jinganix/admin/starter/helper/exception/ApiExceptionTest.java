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
  @DisplayName("of")
  class Of {

    @Test
    @DisplayName("Given error code and message -> returns bad request exception")
    void givenErrorCodeAndMessage() {
      ApiException exception = ApiException.of(ErrorCode.BAD_REQUEST, "invalid");

      assertThat(exception.getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
      assertThat(exception.getMessage()).isEqualTo("invalid");
      assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Given status and error code -> returns exception")
    void givenStatusAndErrorCode() {
      ApiException exception = ApiException.of(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);

      assertThat(exception.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
      assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Given error code only -> returns bad request exception")
    void givenErrorCodeOnly() {
      ApiException exception = ApiException.of(ErrorCode.ERROR);

      assertThat(exception.getCode()).isEqualTo(ErrorCode.ERROR);
      assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Nested
  @DisplayName("fillInStackTrace")
  class FillInStackTrace {

    @Test
    @DisplayName("Given exception -> returns same instance without stack trace")
    void givenException() {
      ApiException exception = ApiException.of(ErrorCode.ERROR);

      assertThat(exception.fillInStackTrace()).isSameAs(exception);
    }
  }
}
