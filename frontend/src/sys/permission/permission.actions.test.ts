import { HttpService } from "@helpers/network/http.service.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { container } from "tsyringe";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { PermissionActions } from "@/sys/permission/permission.actions.ts";

describe("PermissionActions", () => {
  let request: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    request = vi.fn();
    vi.spyOn(container, "resolve").mockReturnValue({ request } as never);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should return options when options request succeeds", async () => {
    const options = [{ code: "USER_READ", label: "Read", value: "1" }];
    request.mockResolvedValue({ options });

    await expect(PermissionActions.getOptions()).resolves.toEqual(options);
    expect(container.resolve).toHaveBeenCalledWith(HttpService);
  });

  it("should return empty list when options request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(PermissionActions.getOptions()).resolves.toEqual([]);
  });

  it("should return paged records when list request succeeds", async () => {
    const records = [
      {
        code: "USER_READ",
        createdAt: 1,
        id: "1",
        name: "Read users",
        status: PermissionStatus.ACTIVE,
        type: PermissionType.UI,
      },
    ];
    request.mockResolvedValue({ page: 0, pages: 1, records, size: 10, total: 1 });

    const result = await PermissionActions.list(DEFAULT_PAGEABLE, { code: "USER_READ" });

    expect(result).toEqual({
      paging: { page: 0, pages: 1, size: 10, total: 1 },
      records,
    });
    expect(request).toHaveBeenCalledOnce();
  });

  it("should return null when list request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(PermissionActions.list(DEFAULT_PAGEABLE, {})).resolves.toBeNull();
  });

  it("should return true when update status succeeds", async () => {
    request.mockResolvedValue({});

    await expect(PermissionActions.updateStatus("1", PermissionStatus.ACTIVE)).resolves.toBe(true);
  });

  it("should return false when update status has no response", async () => {
    request.mockResolvedValue(null);

    await expect(PermissionActions.updateStatus("1", PermissionStatus.INACTIVE)).resolves.toBe(
      false,
    );
  });

  it("should return false without http when delete ids are empty", async () => {
    vi.mocked(container.resolve).mockClear();

    await expect(PermissionActions.delete([])).resolves.toBe(false);
    await expect(PermissionActions.delete([undefined])).resolves.toBe(false);

    expect(container.resolve).not.toHaveBeenCalled();
  });

  it("should return true when delete request succeeds", async () => {
    request.mockResolvedValue({});

    await expect(PermissionActions.delete(["1", undefined])).resolves.toBe(true);
    expect(request).toHaveBeenCalledOnce();
  });

  it("should return false when delete request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(PermissionActions.delete(["1"])).resolves.toBe(false);
  });

  it("should return permission when update succeeds", async () => {
    const permission = {
      code: "USER_READ",
      createdAt: 1,
      id: "1",
      name: "Read users",
      status: PermissionStatus.ACTIVE,
      type: PermissionType.UI,
    };
    request.mockResolvedValue({ permission });

    await expect(
      PermissionActions.update("1", {
        code: "USER_READ",
        name: "Read users",
        status: PermissionStatus.ACTIVE,
        type: PermissionType.UI,
      }),
    ).resolves.toEqual(permission);
  });

  it("should return null when update has no response", async () => {
    request.mockResolvedValue(null);

    await expect(
      PermissionActions.update("1", {
        code: "USER_READ",
        name: "Read users",
        status: PermissionStatus.ACTIVE,
        type: PermissionType.UI,
      }),
    ).resolves.toBeNull();
  });

  it("should return true when create succeeds", async () => {
    request.mockResolvedValue({});

    await expect(
      PermissionActions.create({
        code: "USER_READ",
        name: "Read users",
        status: PermissionStatus.ACTIVE,
        type: PermissionType.UI,
      }),
    ).resolves.toBe(true);
  });

  it("should return false when create has no response", async () => {
    request.mockResolvedValue(null);

    await expect(
      PermissionActions.create({
        code: "USER_READ",
        name: "Read users",
        status: PermissionStatus.ACTIVE,
        type: PermissionType.UI,
      }),
    ).resolves.toBe(false);
  });
});
