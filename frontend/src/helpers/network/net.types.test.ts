import { ErrorCode } from "@proto/ErrorCodeEnum";
import { describe, expect, it, vi } from "vitest";
import { AuthToken, NetError } from "@/helpers/network/net.types.ts";

describe("net.types", () => {
  it("should treat token as expired within 30 second buffer", () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2020-01-01T00:00:00Z"));

    const token = new AuthToken(Date.parse("2020-01-01T00:00:00Z"));
    token.expiresIn = 60;

    expect(token.isExpired()).toBe(false);

    vi.setSystemTime(new Date("2020-01-01T00:00:31Z"));
    expect(token.isExpired()).toBe(true);

    vi.useRealTimers();
  });

  it("should create NetError with code and status", () => {
    const err = new NetError(ErrorCode.ERROR, 500, "server");

    expect(err).toBeInstanceOf(Error);
    expect(err.code).toBe(ErrorCode.ERROR);
    expect(err.status).toBe(500);
    expect(err.message).toBe("server");
  });

  it("should resolve string error code keys", () => {
    const err = new NetError("ERROR");

    expect(err.code).toBe(ErrorCode.ERROR);
  });
});
