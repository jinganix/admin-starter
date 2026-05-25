import { ErrorCode } from "@proto/ErrorCodeEnum";
import { ErrorMessage } from "@proto/ErrorProto";
import { AuthLoginRequest, AuthTokenResponse } from "@proto/SysAuthProto";
import { AxiosError } from "axios";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { environment } from "@/helpers/environment.ts";
import { emitter } from "@/helpers/event/emitter.ts";
import { defaultErrorHandler, HttpService } from "@/helpers/network/http.service.ts";
import { AuthToken, NetError } from "@/helpers/network/net.types.ts";
import { TokenService } from "@/helpers/network/token.service.ts";
import { urlJoin } from "@/helpers/network/url.join.ts";

const { axiosRequest, resolveMock } = vi.hoisted(() => ({
  axiosRequest: vi.fn(),
  resolveMock: vi.fn(),
}));

vi.mock("@/helpers/environment.ts", () => ({
  environment: {
    apiHost: "http://127.0.0.1:8080",
    appTitle: "Admin Starter",
    env: "test",
  },
}));

vi.mock("axios", async (importActual) => {
  const actual = await importActual<typeof import("axios")>();
  const axiosInstance = actual.default;
  return {
    ...actual,
    default: new Proxy(axiosInstance, {
      get(target, property, receiver) {
        if (property === "request") {
          return axiosRequest;
        }
        return Reflect.get(target, property, receiver);
      },
    }),
  };
});

vi.mock("tsyringe", async (importActual) => {
  const actual = await importActual<typeof import("tsyringe")>();
  return {
    ...actual,
    container: {
      ...actual.container,
      resolve: resolveMock,
    },
  };
});

describe("HttpService", () => {
  const service = new HttpService();
  const loginMessage = AuthLoginRequest.create({ password: "p", username: "u" });

  beforeEach(() => {
    axiosRequest.mockReset();
    resolveMock.mockReset();
    vi.spyOn(console, "log").mockImplementation(() => {});
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should resolve token service from container when token service is omitted", async () => {
    const tokenService = { getToken: vi.fn().mockResolvedValue(null) };
    resolveMock.mockReturnValue(tokenService);
    const responseAlias = AuthTokenResponse.create({
      accessToken: "a",
      expiresIn: 3600,
      refreshToken: "r",
    }).toWebpbAlias();
    axiosRequest.mockResolvedValue({ data: responseAlias });

    await service.request(loginMessage, AuthTokenResponse, null);

    expect(resolveMock).toHaveBeenCalledWith(TokenService);
    expect(tokenService.getToken).toHaveBeenCalled();
  });

  it("should attach bearer authorization when token service returns token", async () => {
    const token = new AuthToken();
    token.accessToken = "access-1";
    const tokenService = { getToken: vi.fn().mockResolvedValue(token) };
    const responseAlias = AuthTokenResponse.create({
      accessToken: "a",
      expiresIn: 3600,
      refreshToken: "r",
    }).toWebpbAlias();
    axiosRequest.mockResolvedValue({ data: responseAlias });

    await service.request(loginMessage, AuthTokenResponse, null, tokenService as never);

    expect(axiosRequest).toHaveBeenCalledWith(
      expect.objectContaining({
        headers: expect.objectContaining({
          authorization: "Bearer access-1",
          "content-type": "application/json;charset=utf-8",
        }),
      }),
    );
  });

  it("should omit authorization when token service returns no token", async () => {
    const tokenService = { getToken: vi.fn().mockResolvedValue(null) };
    axiosRequest.mockResolvedValue({ data: {} });

    await service.request(loginMessage, AuthTokenResponse, null, tokenService as never);

    const call = axiosRequest.mock.calls[0][0] as { headers: Record<string, string> };
    expect(call.headers.authorization).toBeUndefined();
  });

  it("should call axios with joined api host context and path when request succeeds", async () => {
    const message = {
      toWebpbAlias: () => ({ a: "u", b: "p" }),
      webpbMeta: () => ({
        class: "AuthLoginRequest",
        context: "api",
        method: "GET",
        path: "/resource",
      }),
    };
    const responseAlias = AuthTokenResponse.create({
      accessToken: "a",
      expiresIn: 3600,
      refreshToken: "r",
    }).toWebpbAlias();
    axiosRequest.mockResolvedValue({ data: responseAlias });
    const tokenService = { getToken: vi.fn().mockResolvedValue(null) };

    await service.request(message as never, AuthTokenResponse, null, tokenService as never);

    expect(axiosRequest).toHaveBeenCalledWith(
      expect.objectContaining({
        data: { a: "u", b: "p" },
        method: "GET",
        responseType: "json",
        timeout: 10000,
        url: urlJoin(environment.apiHost, "api", "/resource"),
      }),
    );
  });

  it("should return parsed response when axios succeeds", async () => {
    const responseAlias = AuthTokenResponse.create({
      accessToken: "access",
      expiresIn: 3600,
      refreshToken: "refresh",
    }).toWebpbAlias();
    axiosRequest.mockResolvedValue({ data: responseAlias });

    const result = await service.request(loginMessage, AuthTokenResponse, null, {
      getToken: vi.fn().mockResolvedValue(null),
    } as never);

    expect(result).toEqual(
      expect.objectContaining({
        accessToken: "access",
        expiresIn: 3600,
        refreshToken: "refresh",
      }),
    );
  });

  it("should invoke error handler and return null when axios fails with net error", async () => {
    const errorHandler = vi.fn();
    const err = new AxiosError("unauthorized");
    err.response = {
      config: {} as never,
      data: ErrorMessage.create({ code: ErrorCode.BAD_TOKEN, errors: {} }).toWebpbAlias(),
      headers: {},
      status: 401,
      statusText: "Unauthorized",
    };
    err.status = 401;
    axiosRequest.mockRejectedValue(err);

    const result = await service.request(loginMessage, AuthTokenResponse, errorHandler, null);

    expect(result).toBeNull();
    expect(errorHandler).toHaveBeenCalledWith(
      expect.objectContaining({ code: ErrorCode.BAD_TOKEN, status: 401 }),
    );
  });

  it("should throw normalized error when error handler is null", async () => {
    const err = new AxiosError("server");
    err.response = {
      config: {} as never,
      data: ErrorMessage.create({ code: ErrorCode.ERROR, errors: {} }).toWebpbAlias(),
      headers: {},
      status: 500,
      statusText: "Error",
    };
    err.status = 500;
    axiosRequest.mockRejectedValue(err);

    await expect(service.request(loginMessage, AuthTokenResponse, null, null)).rejects.toEqual(
      expect.objectContaining({ code: ErrorCode.ERROR, status: 500 }),
    );
  });

  it("should throw null when normalized error is not a net error", async () => {
    axiosRequest.mockRejectedValue(null);

    await expect(
      service.request(loginMessage, AuthTokenResponse, vi.fn(), null),
    ).rejects.toBeNull();
  });

  it("should emit error event when default error handler receives net error", async () => {
    const emitSpy = vi.spyOn(emitter, "emit");
    const netError = new NetError(ErrorCode.ERROR, 500);

    await defaultErrorHandler(netError);

    expect(emitSpy).toHaveBeenCalledWith("error", ErrorCode.ERROR, 500);
  });
});
