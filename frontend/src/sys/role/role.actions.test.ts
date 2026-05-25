import { HttpService } from "@helpers/network/http.service.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { container } from "tsyringe";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { RoleActions } from "@/sys/role/role.actions.ts";

describe("RoleActions", () => {
  let request: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    request = vi.fn();
    vi.spyOn(container, "resolve").mockReturnValue({ request } as never);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should return options when options request succeeds", async () => {
    const options = [{ label: "Admin", value: "1" }];
    request.mockResolvedValue({ options });

    await expect(RoleActions.getOptions()).resolves.toEqual(options);
    expect(container.resolve).toHaveBeenCalledWith(HttpService);
  });

  it("should return empty list when options request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(RoleActions.getOptions()).resolves.toEqual([]);
  });

  it("should return paged records when list request succeeds", async () => {
    const records = [
      {
        code: "ADMIN",
        createdAt: 1,
        id: "1",
        name: "Admin",
        permissionIds: [],
        status: RoleStatus.ACTIVE,
      },
    ];
    request.mockResolvedValue({ page: 0, pages: 1, records, size: 10, total: 1 });

    const result = await RoleActions.list(DEFAULT_PAGEABLE, { name: "Admin" });

    expect(result).toEqual({
      paging: { page: 0, pages: 1, size: 10, total: 1 },
      records,
    });
  });

  it("should return null when list request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(RoleActions.list(DEFAULT_PAGEABLE, {})).resolves.toBeNull();
  });

  it("should return true when update status succeeds", async () => {
    request.mockResolvedValue({});

    await expect(RoleActions.updateStatus("1", RoleStatus.ACTIVE)).resolves.toBe(true);
  });

  it("should return false when update status has no response", async () => {
    request.mockResolvedValue(null);

    await expect(RoleActions.updateStatus("1", RoleStatus.INACTIVE)).resolves.toBe(false);
  });

  it("should return false without http when delete ids are empty", async () => {
    vi.mocked(container.resolve).mockClear();

    await expect(RoleActions.delete([])).resolves.toBe(false);
    await expect(RoleActions.delete([undefined])).resolves.toBe(false);

    expect(container.resolve).not.toHaveBeenCalled();
  });

  it("should return true when delete request succeeds", async () => {
    request.mockResolvedValue({});

    await expect(RoleActions.delete(["1", undefined])).resolves.toBe(true);
    expect(request).toHaveBeenCalledOnce();
  });

  it("should return false when delete request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(RoleActions.delete(["1"])).resolves.toBe(false);
  });

  it("should return role when update succeeds", async () => {
    const role = {
      code: "ADMIN",
      createdAt: 1,
      id: "1",
      name: "Admin",
      permissionIds: [],
      status: RoleStatus.ACTIVE,
    };
    request.mockResolvedValue({ role });

    await expect(
      RoleActions.update("1", {
        code: "ADMIN",
        name: "Admin",
        permissionIds: [],
        status: RoleStatus.ACTIVE,
      }),
    ).resolves.toEqual(role);
  });

  it("should return null when update has no response", async () => {
    request.mockResolvedValue(null);

    await expect(
      RoleActions.update("1", {
        code: "ADMIN",
        name: "Admin",
        permissionIds: [],
        status: RoleStatus.ACTIVE,
      }),
    ).resolves.toBeNull();
  });

  it("should return true when create succeeds", async () => {
    request.mockResolvedValue({});

    await expect(
      RoleActions.create({
        code: "ADMIN",
        name: "Admin",
        permissionIds: [],
        status: RoleStatus.ACTIVE,
      }),
    ).resolves.toBe(true);
  });

  it("should return false when create has no response", async () => {
    request.mockResolvedValue(null);

    await expect(
      RoleActions.create({
        code: "ADMIN",
        name: "Admin",
        permissionIds: [],
        status: RoleStatus.ACTIVE,
      }),
    ).resolves.toBe(false);
  });
});
