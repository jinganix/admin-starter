import { describe, expect, it } from "vitest";
import { urlJoin } from "@/helpers/network/url.join.ts";

describe("urlJoin", () => {
  it("should join path segments", () => {
    expect(urlJoin("http://api.test", "v1", "users")).toBe("http://api.test/v1/users");
  });

  it("should normalize duplicate slashes", () => {
    expect(urlJoin("http://api.test/", "/v1/", "/users/")).toBe("http://api.test/v1/users/");
  });

  it("should preserve query and hash segments", () => {
    expect(urlJoin("http://api.test", "search?q=1", "extra")).toContain("search");
  });

  it("should return empty string for empty input", () => {
    expect(urlJoin()).toBe("");
  });

  it("should throw when first segment is not a string", () => {
    expect(() => urlJoin(null as unknown as string)).toThrow(TypeError);
  });

  it("should join array input and file protocol paths", () => {
    expect(urlJoin(["http://api.test", "v1"] as unknown as string)).toBe("http://api.test/v1");
    expect(urlJoin("file:///tmp", "data")).toContain("file:///");
  });

  it("should merge protocol-only first segment with next part", () => {
    expect(urlJoin("http:", "api.test", "v1")).toBe("http://api.test/v1");
  });

  it("should join path segments that include query strings", () => {
    expect(urlJoin("http://api.test", "path?a=1", "b=2")).toBe("http://api.test/path?a=1/b=2");
  });

  it("should preserve hash segments", () => {
    expect(urlJoin("http://api.test", "page#section")).toBe("http://api.test/page#section");
  });
});
