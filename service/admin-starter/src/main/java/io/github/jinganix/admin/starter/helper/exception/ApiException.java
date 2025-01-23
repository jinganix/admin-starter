package io.github.jinganix.admin.starter.helper.exception;

import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiException extends RuntimeException {

  private final HttpStatusCode status;

  private final ErrorCode code;

  private ApiException(HttpStatusCode httpStatus, ErrorCode errorCode, String message) {
    super(message);
    this.status = httpStatus;
    this.code = errorCode;
  }

  /**
   * Create an exception.
   *
   * @param error {@link ErrorCode}
   * @param message message
   * @return {@link ApiException}
   */
  public static ApiException of(ErrorCode error, String message) {
    return new ApiException(HttpStatus.BAD_REQUEST, error, message);
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link ErrorCode}
   * @return {@link ApiException}
   */
  public static ApiException of(HttpStatusCode status, ErrorCode errorCode) {
    return new ApiException(status, errorCode, null);
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link ErrorCode}
   * @return {@link ApiException}
   */
  public static ApiException of(ErrorCode errorCode) {
    return new ApiException(HttpStatus.BAD_REQUEST, errorCode, null);
  }

  /**
   * Not fill the stacktrace.
   *
   * @return {@link Throwable}
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
