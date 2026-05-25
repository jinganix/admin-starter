import { ErrorCode } from "@proto/ErrorCodeEnum";
import { ErrorMessage } from "@proto/ErrorProto";
import { AxiosError } from "axios";
import { describe, expect, it } from "vitest";
import { ErrorNormalizer } from "@/helpers/network/error.normalizer.ts";
import { NetError } from "@/helpers/network/net.types.ts";

describe("ErrorNormalizer", () => {
  const normalizer = new ErrorNormalizer();

  it("should return null when error is falsy", () => {
    expect(normalizer.normalize(null)).toBeNull();
  });

  it("should pass through existing NetError", () => {
    const err = new NetError(ErrorCode.ERROR);
    expect(normalizer.normalize(err)).toBe(err);
  });

  it("should map axios network error", () => {
    const err = new AxiosError("network");
    err.code = AxiosError.ERR_NETWORK;

    const result = normalizer.normalize(err) as NetError;
    expect(result.code).toBe(ErrorCode.ERROR_NETWORK);
  });

  it("should map axios timeout error", () => {
    const err = new AxiosError("timeout");
    err.code = AxiosError.ETIMEDOUT;

    const result = normalizer.normalize(err) as NetError;
    expect(result.code).toBe(ErrorCode.REQUEST_TIMEOUT);
  });

  it("should map response body with error code", () => {
    const err = new AxiosError("bad");
    err.response = {
      config: {} as never,
      data: ErrorMessage.create({ code: ErrorCode.BAD_TOKEN, errors: {} }),
      headers: {},
      status: 401,
      statusText: "Unauthorized",
    };

    err.status = 401;
    const result = normalizer.normalize(err) as NetError;
    expect(result.code).toBe(ErrorCode.BAD_TOKEN);
    expect(result.status).toBe(401);
  });

  it("should map timeout and access denied messages", () => {
    expect((normalizer.normalize({ message: "timeout" }) as NetError).code).toBe(
      ErrorCode.REQUEST_TIMEOUT,
    );
    expect((normalizer.normalize({ message: "Access Denied" }) as NetError).code).toBe(
      ErrorCode.ACCESS_DENIED,
    );
  });

  it("should parse json message payload", () => {
    const payload = JSON.stringify(
      ErrorMessage.create({ code: ErrorCode.ERROR, errors: {} }).toWebpbAlias(),
    );
    const result = normalizer.normalize({ message: payload }) as NetError;

    expect(result.code).toBe(ErrorCode.ERROR);
  });

  it("should fallback to generic error", () => {
    const result = normalizer.normalize({}) as NetError;
    expect(result.code).toBe(ErrorCode.ERROR);
  });

  it("should map axios response without error code to generic error", () => {
    const err = new AxiosError("bad");
    err.response = {
      config: {} as never,
      data: { message: "no code" },
      headers: {},
      status: 500,
      statusText: "Error",
    };

    const result = normalizer.normalize(err) as NetError;
    expect(result.code).toBe(ErrorCode.ERROR);
  });

  it("should map string errors and unknown messages", () => {
    expect((normalizer.normalize("plain error") as NetError).code).toBe(ErrorCode.ERROR);
    expect((normalizer.normalize({ message: "other" }) as NetError).code).toBe(ErrorCode.ERROR);
  });

  it("should return null data message when response body has no code", () => {
    const result = normalizer.normalize({ message: "not json" }) as NetError;
    expect(result.code).toBe(ErrorCode.ERROR);
  });
});
