import { describe, expect, it } from "vitest";
import { paginationData } from "@/components/table/table.pagination.tsx";

describe("paginationData", () => {
  it("should list all pages when total is seven or fewer", () => {
    expect(paginationData(1, 5).map((x) => x.page)).toEqual([1, 2, 3, 4, 5]);
  });

  it("should show start ellipsis pattern when page is near beginning", () => {
    expect(paginationData(2, 10).map((x) => x.page)).toEqual([1, 2, 3, -1, 8, 9, 10]);
  });

  it("should include page four when current page is three", () => {
    expect(paginationData(3, 10).map((x) => x.page)).toEqual([1, 2, 3, 4, -1, 9, 10]);
  });

  it("should show near-end pattern when page is two from last", () => {
    expect(paginationData(8, 10).map((x) => x.page)).toEqual([1, 2, -1, 7, 8, 9, 10]);
  });

  it("should show middle pattern when page is in center", () => {
    expect(paginationData(5, 10).map((x) => x.page)).toEqual([1, -1, 4, 5, 6, -1, 10]);
  });

  it("should show end ellipsis pattern when page is near end", () => {
    expect(paginationData(9, 10).map((x) => x.page)).toEqual([1, 2, 3, -1, 8, 9, 10]);
  });

  it("should mark selected page", () => {
    const items = paginationData(4, 10);
    expect(items.find((x) => x.page === 4)?.selected).toBe(true);
    expect(items.find((x) => x.page === 3)?.selected).toBe(false);
  });

  it("should omit href for ellipsis items", () => {
    const ellipsis = paginationData(5, 10).find((x) => x.page === -1);
    expect(ellipsis?.href).toBeUndefined();
  });
});
