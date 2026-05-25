import { HttpService } from "@helpers/network/http.service.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { container } from "tsyringe";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AuditActions } from "@/sys/audit/audit.actions.ts";

describe("AuditActions", () => {
  let request: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    request = vi.fn();
    vi.spyOn(container, "resolve").mockReturnValue({ request } as never);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should return paged records when list request succeeds", async () => {
    const records = [
      {
        createdAt: 1,
        id: "1",
        method: "GET",
        path: "/users",
        userId: "u1",
        username: "user",
      },
    ];
    request.mockResolvedValue({ page: 0, pages: 1, records, size: 10, total: 1 });

    const result = await AuditActions.list(DEFAULT_PAGEABLE, { userId: "u1" });

    expect(result).toEqual({
      paging: { page: 0, pages: 1, size: 10, total: 1 },
      records,
    });
    expect(container.resolve).toHaveBeenCalledWith(HttpService);
    expect(request).toHaveBeenCalledOnce();
  });

  it("should return null when list request has no response", async () => {
    request.mockResolvedValue(null);

    await expect(AuditActions.list(DEFAULT_PAGEABLE, {})).resolves.toBeNull();
  });
});
