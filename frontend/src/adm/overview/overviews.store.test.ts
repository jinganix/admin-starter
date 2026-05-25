import { container } from "tsyringe";
import { describe, expect, it, vi } from "vitest";
import { OverviewsStore } from "@/adm/overview/overviews.store.ts";

describe("OverviewsStore", () => {
  it("should calculate totals and chart slices from records", async () => {
    const store = new OverviewsStore();
    const listener = vi.fn();
    store.subscribe(listener);
    vi.spyOn(container, "resolve").mockReturnValue({
      request: vi.fn().mockResolvedValue({
        records: [
          {
            apiGet: 1,
            apiPost: 2,
            month: "2024-01-01",
            permissionCreated: 1,
            permissionDeleted: 0,
            roleCreated: 2,
            roleDeleted: 1,
            userCreated: 3,
            userDeleted: 1,
          },
          {
            apiGet: 2,
            apiPost: 1,
            month: "2024-02-01",
            permissionCreated: 0,
            permissionDeleted: 1,
            roleCreated: 1,
            roleDeleted: 0,
            userCreated: 1,
            userDeleted: 0,
          },
        ],
      }),
    } as never);

    await store.load();

    expect(store.apiGet).toBe(3);
    expect(store.apiPost).toBe(3);
    expect(store.userCreated).toBe(4);
    expect(store.apiData.records.length).toBeLessThanOrEqual(12);
    expect(listener).toHaveBeenCalled();
  });
});
