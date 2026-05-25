import { describe, expect, it } from "vitest";
import { Deferred } from "@/helpers/network/deferred.ts";

describe("Deferred", () => {
  it("should resolve promise when resolve is called", async () => {
    const deferred = new Deferred<string>();
    deferred.resolve("ok");

    await expect(deferred.promise).resolves.toBe("ok");
  });

  it("should reject promise when reject is called", async () => {
    const deferred = new Deferred<void>();
    deferred.reject(new Error("failed"));

    await expect(deferred.promise).rejects.toThrow("failed");
  });
});
