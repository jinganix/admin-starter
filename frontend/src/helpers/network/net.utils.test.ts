import { describe, expect, it } from "vitest";
import { NetError } from "@/helpers/network/net.types.ts";
import { fromAlias, isNetError, tryJsonParse } from "@/helpers/network/net.utils.ts";

describe("net.utils", () => {
  it("should parse valid json", () => {
    expect(tryJsonParse('{"a":1}')).toEqual({ a: 1 });
  });

  it("should return null when json is invalid", () => {
    expect(tryJsonParse("not-json")).toBeNull();
  });

  it("should use fromAlias when message type provides it", () => {
    const messageType = {
      fromAlias: (data: unknown) => ({ mapped: data }),
      prototype: {},
    };

    expect(fromAlias({ x: 1 }, messageType as never)).toEqual({ mapped: { x: 1 } });
  });

  it("should return data as-is when fromAlias is missing", () => {
    expect(fromAlias({ x: 1 })).toEqual({ x: 1 });
  });

  it("should detect NetError instances", () => {
    expect(isNetError(new NetError("ERROR"))).toBe(true);
    expect(isNetError(new Error("x"))).toBe(false);
  });
});
