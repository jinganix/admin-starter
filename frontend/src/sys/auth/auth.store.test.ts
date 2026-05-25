import { HttpService } from "@helpers/network/http.service.ts";
import { TokenService } from "@helpers/network/token.service.ts";
import { UserStatus } from "@proto/SysUserProto";
import { container } from "tsyringe";
import { afterEach, describe, expect, it, vi } from "vitest";
import { AuthToken } from "@/helpers/network/net.types.ts";
import { AuthStore } from "@/sys/auth/auth.store.ts";

describe("AuthStore", () => {
  const store = new AuthStore();

  afterEach(() => {
    store.dispose();
    vi.restoreAllMocks();
  });

  it("should notify subscribers when user is updated", () => {
    const listener = vi.fn();
    store.subscribe(listener);

    store.update({
      createdAt: 0,
      id: "1",
      nickname: "n",
      status: UserStatus.ACTIVE,
      username: "u",
    });

    expect(store.getVersion()).toBeGreaterThan(0);
    expect(listener).toHaveBeenCalled();
  });

  it("should split role and permission authorities", () => {
    store.updateAuthorities(["ROLE_ADMIN", "USER_READ"]);

    expect(store.hasRole("ADMIN")).toBe(true);
    expect(store.hasAuthority("USER_READ")).toBe(true);
  });

  it("should grant all authorities for admin role", () => {
    store.updateAuthorities(["ROLE_ADMIN"]);

    expect(store.hasAuthority("ANY_PERMISSION")).toBe(true);
  });

  it("should report authed only when user id exists", () => {
    expect(store.isAuthed()).toBe(false);
    store.id = "1";
    expect(store.isAuthed()).toBe(true);
  });

  it("should dispose and reset state", () => {
    store.id = "1";
    store.dispose();

    expect(store.isAuthed()).toBe(false);
    expect(store.roles.size).toBe(0);
  });

  it("should initialize and load current user when token exists", async () => {
    vi.spyOn(container, "resolve").mockImplementation((token) => {
      if (token === TokenService) {
        return { getToken: vi.fn().mockResolvedValue(new AuthToken()) } as never;
      }
      return {
        request: vi.fn().mockResolvedValue({
          user: {
            authorities: ["ROLE_ADMIN"],
            id: "1",
            nickname: "n",
            status: UserStatus.ACTIVE,
            username: "u",
          },
        }),
      } as never;
    });

    await store.initialize();

    expect(store.isAuthed()).toBe(true);
    expect(container.resolve).toHaveBeenCalledWith(HttpService);
  });
});
