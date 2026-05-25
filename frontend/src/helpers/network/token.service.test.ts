import {
  AuthLoginRequest,
  AuthSignupRequest,
  AuthTokenRequest,
  AuthTokenResponse,
} from "@proto/SysAuthProto";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { HttpService } from "@/helpers/network/http.service.ts";
import { AuthToken } from "@/helpers/network/net.types.ts";
import { TokenService } from "@/helpers/network/token.service.ts";

const TOKEN_KEY = "AUTH_TOKEN";

const { httpRequest, resolveMock } = vi.hoisted(() => ({
  httpRequest: vi.fn(),
  resolveMock: vi.fn(),
}));

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

vi.mock("uuid", () => ({
  v4: () => "login-key",
}));

describe("TokenService", () => {
  const storage = new Map<string, string>();

  beforeEach(() => {
    vi.useFakeTimers();
    storage.clear();
    httpRequest.mockReset();
    resolveMock.mockReset();
    resolveMock.mockImplementation((token) => {
      if (token === HttpService) {
        return { request: httpRequest };
      }
      throw new Error(`unexpected resolve token: ${String(token)}`);
    });
    vi.stubGlobal("localStorage", {
      clear: () => storage.clear(),
      getItem: (key: string) => storage.get(key) ?? null,
      removeItem: (key: string) => {
        storage.delete(key);
      },
      setItem: (key: string, value: string) => {
        storage.set(key, value);
      },
    });
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  function tokenResponse(overrides?: Partial<AuthTokenResponse>): AuthTokenResponse {
    return AuthTokenResponse.create({
      accessToken: "access",
      expiresIn: 3600,
      refreshToken: "refresh",
      ...overrides,
    });
  }

  function freshToken(overrides?: Partial<AuthToken>): AuthToken {
    const token = new AuthToken();
    token.accessToken = overrides?.accessToken ?? "access";
    token.refreshToken = overrides?.refreshToken ?? "refresh";
    token.expiresIn = overrides?.expiresIn ?? 3600;
    return token;
  }

  function expiredToken(): AuthToken {
    const token = new AuthToken(Date.now() - 120_000);
    token.accessToken = "expired-access";
    token.refreshToken = "expired-refresh";
    token.expiresIn = 1;
    return token;
  }

  it("should return null from readToken when storage is empty", () => {
    const service = new TokenService();

    expect(service.readToken()).toBeNull();
  });

  it("should return null from readToken when stored json is corrupt", () => {
    storage.set(TOKEN_KEY, "{not-json");

    const service = new TokenService();

    expect(service.readToken()).toBeNull();
  });

  it("should return token from readToken when storage has valid json", () => {
    const saved = freshToken({ accessToken: "stored-access" });
    storage.set(TOKEN_KEY, JSON.stringify(saved));

    const service = new TokenService();

    expect(service.readToken()?.accessToken).toBe("stored-access");
  });

  it("should persist token when auth succeeds", async () => {
    httpRequest.mockResolvedValue(tokenResponse({ accessToken: "auth-access" }));
    const service = new TokenService();

    const token = await service.auth("user", "pass");

    expect(token?.accessToken).toBe("auth-access");
    expect(storage.get(TOKEN_KEY)).toContain("auth-access");
    expect(httpRequest).toHaveBeenCalledWith(
      expect.any(AuthLoginRequest),
      AuthTokenResponse,
      expect.any(Function),
      null,
    );
    expect(resolveMock).toHaveBeenCalledWith(HttpService);
  });

  it("should clear replay when auth returns no token", async () => {
    httpRequest.mockResolvedValue(null);
    const service = new TokenService();

    const token = await service.auth("user", "pass");

    expect(token).toBeNull();
    expect(await service.getToken()).toBeNull();
  });

  it("should persist token when signup succeeds", async () => {
    httpRequest.mockResolvedValue(tokenResponse({ accessToken: "signup-access" }));
    const service = new TokenService();

    const token = await service.signup("user", "pass");

    expect(token?.accessToken).toBe("signup-access");
    expect(httpRequest).toHaveBeenCalledWith(
      expect.any(AuthSignupRequest),
      AuthTokenResponse,
      expect.any(Function),
      null,
    );
  });

  it("should clear replay when signup returns no token", async () => {
    httpRequest.mockResolvedValue(null);
    const service = new TokenService();

    const token = await service.signup("user", "pass");

    expect(token).toBeNull();
    expect(await service.getToken()).toBeNull();
  });

  it("should return cached token from getToken when token is not expired", async () => {
    storage.set(TOKEN_KEY, JSON.stringify(freshToken({ accessToken: "cached" })));
    const service = new TokenService();

    const token = await service.getToken();

    expect(token?.accessToken).toBe("cached");
    expect(httpRequest).not.toHaveBeenCalled();
  });

  it("should refresh token from getToken when cached token is expired", async () => {
    storage.set(TOKEN_KEY, JSON.stringify(expiredToken()));
    httpRequest.mockResolvedValue(tokenResponse({ accessToken: "refreshed" }));
    const service = new TokenService();

    const token = await service.getToken();

    expect(token?.accessToken).toBe("refreshed");
    expect(httpRequest).toHaveBeenCalledWith(
      expect.any(AuthTokenRequest),
      AuthTokenResponse,
      expect.any(Function),
      null,
    );
  });

  it("should persist token when refresh succeeds", async () => {
    httpRequest.mockResolvedValue(tokenResponse({ accessToken: "new-access" }));
    const service = new TokenService();

    const token = await service.refresh("refresh-token");

    expect(token?.accessToken).toBe("new-access");
    expect(storage.get(TOKEN_KEY)).toContain("new-access");
  });

  it("should remove stored token when refresh returns no token", async () => {
    storage.set(TOKEN_KEY, JSON.stringify(freshToken()));
    httpRequest.mockResolvedValue(null);
    const service = new TokenService();

    const token = await service.refresh("refresh-token");

    expect(token).toBeNull();
    expect(storage.has(TOKEN_KEY)).toBe(false);
  });

  it("should remove stored token and reset replay when deleteToken is called", async () => {
    httpRequest.mockResolvedValue(tokenResponse());
    const service = new TokenService();
    await service.auth("user", "pass");

    await service.deleteToken();

    expect(storage.has(TOKEN_KEY)).toBe(false);
    expect(await service.getToken()).toBeNull();
  });

  it("should refresh expired token when checkRefresh interval fires", async () => {
    storage.set(TOKEN_KEY, JSON.stringify(expiredToken()));
    httpRequest.mockResolvedValue(tokenResponse({ accessToken: "interval-refresh" }));
    new TokenService();

    await vi.advanceTimersByTimeAsync(10_000);

    expect(httpRequest).toHaveBeenCalled();
    expect(storage.get(TOKEN_KEY)).toContain("interval-refresh");
  });
});
