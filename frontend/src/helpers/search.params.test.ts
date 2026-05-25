import { describe, expect, it } from "vitest";
import { SortDirection } from "@/helpers/paging/pageable.ts";
import {
  defaultSearchParams,
  FormValuesResolver,
  getPage,
  getPageSize,
  mergeParams,
  overrideParams,
  paramsEquals,
  toPageable,
  toSearchParams,
} from "@/helpers/search.params.ts";

describe("search.params", () => {
  it("should remove empty values when merging params by default", () => {
    const params = new URLSearchParams("keep=1&drop=");

    const result = mergeParams(params, { drop: "", empty: null, keep: "2", missing: undefined });

    expect(result.get("keep")).toBe("2");
    expect(result.has("drop")).toBe(false);
    expect(result.has("empty")).toBe(false);
    expect(result.has("missing")).toBe(false);
  });

  it("should serialize arrays and objects when merging params", () => {
    const result = mergeParams(new URLSearchParams(), {
      ids: ["a", "b"],
      sort: { createdAt: SortDirection.desc, id: SortDirection.asc },
    });

    expect(result.get("ids")).toBe("a,b");
    expect(result.get("sort")).toBe("createdAt,desc;id,asc");
  });

  it("should keep keys when override does not use remove predicate", () => {
    const result = overrideParams(new URLSearchParams("tag=old"), { page: 0, tag: "new" });

    expect(result.get("page")).toBe("0");
    expect(result.get("tag")).toBe("new");
  });

  it("should build search params from object", () => {
    expect(toSearchParams({ page: 2, size: 20 }).get("page")).toBe("2");
  });

  it("should parse pageable from search params", () => {
    const params = new URLSearchParams("page=1&size=25&sort=createdAt,desc;id,asc");

    expect(toPageable(params)).toEqual({
      page: 1,
      size: 25,
      sort: { createdAt: SortDirection.desc, id: SortDirection.asc },
    });
  });

  it("should use defaults when page and size are missing", () => {
    expect(getPage(new URLSearchParams())).toBe(0);
    expect(getPageSize(new URLSearchParams())).toBe(10);
  });

  it("should compare params for equality", () => {
    const a = new URLSearchParams("a=1&b=2");
    const b = new URLSearchParams("b=2&a=1");

    expect(paramsEquals(a, b)).toBe(true);
    expect(paramsEquals(a, new URLSearchParams("a=2"))).toBe(false);
  });

  it("should resolve form values from params with defaults", () => {
    const resolver = new FormValuesResolver({
      name: ["", (x) => x ?? ""],
      page: [0, Number],
    });

    expect(resolver.resolve(new URLSearchParams("name=alice&page=3"))).toEqual({
      name: "alice",
      page: 3,
    });
    expect(resolver.resolve()).toEqual({ name: "", page: 0 });
  });

  it("should create default search params with sort", () => {
    const params = defaultSearchParams();

    expect(params.get("page")).toBe("0");
    expect(params.get("size")).toBe("10");
    expect(params.get("sort")).toContain("createdAt,desc");
  });
});
