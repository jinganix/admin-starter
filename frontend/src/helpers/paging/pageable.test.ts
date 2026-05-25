import { describe, expect, it } from "vitest";
import { DEFAULT_PAGEABLE, DEFAULT_PAGING, pbToPaging } from "@/helpers/paging/pageable.ts";

describe("pageable", () => {
  it("should expose default pageable and paging", () => {
    expect(DEFAULT_PAGEABLE.page).toBe(0);
    expect(DEFAULT_PAGEABLE.size).toBe(10);
    expect(DEFAULT_PAGING.total).toBe(0);
  });

  it("should map protobuf paging to paging", () => {
    expect(pbToPaging({ page: 2, pages: 5, size: 20, total: 100 })).toEqual({
      page: 2,
      pages: 5,
      size: 20,
      total: 100,
    });
  });
});
