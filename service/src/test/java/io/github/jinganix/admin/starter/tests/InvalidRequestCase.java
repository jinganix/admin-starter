package io.github.jinganix.admin.starter.tests;

import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;

public record InvalidRequestCase<R>(ErrorCode errorCode, R request, String description) {

  public static <R> InvalidRequestCase<R> of(ErrorCode errorCode, R request, String description) {
    return new InvalidRequestCase<>(errorCode, request, description);
  }

  public static <R> InvalidRequestCase<R> badRequest(R request, String description) {
    return of(ErrorCode.BAD_REQUEST, request, description);
  }

  @Override
  public String toString() {
    return description;
  }
}
