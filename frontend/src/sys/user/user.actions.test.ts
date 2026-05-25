import { HttpService } from "@helpers/network/http.service.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { UserStatus } from "@proto/SysUserProto.ts";
import { container } from "tsyringe";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { authStore } from "@/sys/auth/auth.store.ts";
import { UserActions } from "@/sys/user/user.actions.ts";

describe("UserActions", () => {
  let request: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    request = vi.fn();
    vi.spyOn(container, "resolve").mockReturnValue({ request } as never);
  });

  afterEach(() => {
    authStore.dispose();
    vi.restoreAllMocks();
  });

  it("should return paged records when list request succeeds", async () => {
    const records = [
      { createdAt: 1, id: "1", nickname: "n", status: UserStatus.ACTIVE, username: "u" },
    ];
    request.mockResolvedValue({ page: 0, pages: 1, records, size: 10, total: 1 });

    const result = await UserActions.list(DEFAULT_PAGEABLE, { username: "u" });

    expect(result).toEqual({
      paging: { page: 0, pages: 1, size: 10, total: 1 },
      records,
    });
    expect(container.resolve).toHaveBeenCalledWith(HttpService);
    expect(request).toHaveBeenCalledOnce();
  });

  it("should return null when list request has no response", async () => {
    request.mockResolvedValue(null);

    const result = await UserActions.list(DEFAULT_PAGEABLE, {});

    expect(result).toBeNull();
  });

  it("should return true when update status succeeds", async () => {
    request.mockResolvedValue({});

    await expect(UserActions.updateStatus("1", UserStatus.ACTIVE)).resolves.toBe(true);
  });

  it("should return false when update status has no response", async () => {
    request.mockResolvedValue(null);

    await expect(UserActions.updateStatus("1", UserStatus.INACTIVE)).resolves.toBe(false);
  });

  it("should return false without http when delete ids are empty", async () => {
    vi.mocked(container.resolve).mockClear();

    await expect(UserActions.delete([])).resolves.toBe(false);
    await expect(UserActions.delete([undefined])).resolves.toBe(false);

    expect(container.resolve).not.toHaveBeenCalled();
  });

  it("should return true when delete request succeeds", async () => {
    request.mockResolvedValue({});

    await expect(UserActions.delete(["1", undefined, "2"])).resolves.toBe(true);
    expect(request).toHaveBeenCalledOnce();
  });

  it("should return false when delete request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(UserActions.delete(["1"])).resolves.toBe(false);
  });

  it("should return user when update succeeds", async () => {
    const user = { createdAt: 1, id: "1", nickname: "n", status: UserStatus.ACTIVE, username: "u" };
    request.mockResolvedValue({ user });

    await expect(
      UserActions.update("1", { nickname: "n", roleIds: [], status: UserStatus.ACTIVE }),
    ).resolves.toEqual(user);
  });

  it("should return null when update has no response", async () => {
    request.mockResolvedValue(null);

    await expect(
      UserActions.update("1", { nickname: "n", roleIds: [], status: UserStatus.ACTIVE }),
    ).resolves.toBeNull();
  });

  it("should return true when create succeeds", async () => {
    request.mockResolvedValue({});

    await expect(
      UserActions.create({
        password: "p",
        roleIds: [],
        status: UserStatus.ACTIVE,
        username: "u",
      }),
    ).resolves.toBe(true);
  });

  it("should return false when create has no response", async () => {
    request.mockResolvedValue(null);

    await expect(
      UserActions.create({
        password: "p",
        roleIds: [],
        status: UserStatus.ACTIVE,
        username: "u",
      }),
    ).resolves.toBe(false);
  });

  it("should return true when change password succeeds", async () => {
    request.mockResolvedValue({});

    await expect(UserActions.changePassword("current", "next")).resolves.toBe(true);
  });

  it("should return false when change password has no response", async () => {
    request.mockResolvedValue(null);

    await expect(UserActions.changePassword("current", "next")).resolves.toBe(false);
  });

  it("should update auth store and return true when profile update succeeds", async () => {
    const user = {
      createdAt: 1,
      id: "1",
      nickname: "new",
      status: UserStatus.ACTIVE,
      username: "u",
    };
    request.mockResolvedValue({ user });
    const update = vi.spyOn(authStore, "update");

    await expect(UserActions.updateProfile("new")).resolves.toBe(true);

    expect(update).toHaveBeenCalledWith(user);
  });

  it("should return false when profile update has no response", async () => {
    request.mockResolvedValue(null);
    const update = vi.spyOn(authStore, "update");

    await expect(UserActions.updateProfile("new")).resolves.toBe(false);

    expect(update).not.toHaveBeenCalled();
  });

  it("should return user when retrieve succeeds", async () => {
    const user = { createdAt: 1, id: "1", nickname: "n", status: UserStatus.ACTIVE, username: "u" };
    request.mockResolvedValue({ user });

    await expect(UserActions.retrieve("1")).resolves.toEqual(user);
  });

  it("should return null when retrieve has no response", async () => {
    request.mockResolvedValue(null);

    await expect(UserActions.retrieve("1")).resolves.toBeNull();
  });
});
