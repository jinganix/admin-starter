import { container } from "tsyringe";
import { describe, expect, it, vi } from "vitest";
import { authStore } from "@/sys/auth/auth.store.ts";
import { logout } from "@/sys/user/user.utils.ts";

describe("logout", () => {
  it("should delete token and dispose auth store", async () => {
    const deleteToken = vi.fn().mockResolvedValue(undefined);
    vi.spyOn(container, "resolve").mockReturnValue({ deleteToken } as never);
    const dispose = vi.spyOn(authStore, "dispose");

    await logout();

    expect(deleteToken).toHaveBeenCalledOnce();
    expect(dispose).toHaveBeenCalledOnce();
  });
});
