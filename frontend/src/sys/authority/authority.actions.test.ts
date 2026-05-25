import { emitter } from "@helpers/event/emitter.ts";
import { HttpService } from "@helpers/network/http.service.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { container } from "tsyringe";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AuthorityActions } from "@/sys/authority/authority.actions.ts";
import { Authority } from "@/sys/authority/authority.ts";

describe("AuthorityActions", () => {
  let request: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    request = vi.fn();
    vi.spyOn(container, "resolve").mockReturnValue({ request } as never);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should upload ui permissions and emit success when request succeeds", async () => {
    request.mockResolvedValue({});
    const emit = vi.spyOn(emitter, "emit");

    await expect(AuthorityActions.uploadUI()).resolves.toBe(true);

    expect(container.resolve).toHaveBeenCalledWith(HttpService);
    expect(request).toHaveBeenCalledOnce();
    expect(emit).toHaveBeenCalledWith("error", ErrorCode.OK);

    const [{ permissions }] = request.mock.calls[0] as [
      { permissions: Array<{ code: string; type: PermissionType; status: PermissionStatus }> },
    ];
    expect(permissions).toHaveLength(Object.values(Authority).length);
    expect(permissions.every((p) => p.type === PermissionType.UI)).toBe(true);
    expect(permissions.every((p) => p.status === PermissionStatus.ACTIVE)).toBe(true);
  });

  it("should return false without emitting when ui upload has no response", async () => {
    request.mockResolvedValue(null);
    const emit = vi.spyOn(emitter, "emit");

    await expect(AuthorityActions.uploadUI()).resolves.toBe(false);

    expect(emit).not.toHaveBeenCalled();
  });

  it("should reload api permissions and emit success when request succeeds", async () => {
    request.mockResolvedValue({});
    const emit = vi.spyOn(emitter, "emit");

    await expect(AuthorityActions.reloadAPI()).resolves.toBe(true);

    expect(container.resolve).toHaveBeenCalledWith(HttpService);
    expect(request).toHaveBeenCalledOnce();
    expect(emit).toHaveBeenCalledWith("error", ErrorCode.OK);
  });

  it("should return false without emitting when api reload has no response", async () => {
    request.mockResolvedValue(null);
    const emit = vi.spyOn(emitter, "emit");

    await expect(AuthorityActions.reloadAPI()).resolves.toBe(false);

    expect(emit).not.toHaveBeenCalled();
  });
});
