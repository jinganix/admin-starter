import { describe, expect, it, vi } from "vitest";
import { Replay } from "@/helpers/network/replay.ts";

describe("Replay", () => {
  it("should return cached value when constructed with initial value", async () => {
    const replay = new Replay("cached");

    await expect(replay.value()).resolves.toBe("cached");
    expect(replay.resolved).toBe(true);
  });

  it("should coalesce concurrent resolve calls for same key", async () => {
    const replay = new Replay<string>();
    const defer = vi.fn().mockImplementation(
      () =>
        new Promise<string>((resolve) => {
          setTimeout(() => resolve("once"), 10);
        }),
    );

    const [a, b] = await Promise.all([replay.resolve(defer, "k"), replay.resolve(defer, "k")]);

    expect(a).toBe("once");
    expect(b).toBe("once");
    expect(defer).toHaveBeenCalledOnce();
  });

  it("should start new resolve when key changes after resolved", async () => {
    const replay = new Replay<string>();
    const first = vi.fn().mockResolvedValue("first");
    const second = vi.fn().mockResolvedValue("second");

    await replay.resolve(first, "a");
    await expect(replay.resolve(second, "b")).resolves.toBe("second");
    expect(first).toHaveBeenCalledOnce();
    expect(second).toHaveBeenCalledOnce();
  });

  it("should return null from value when unset", async () => {
    const replay = new Replay<string>();

    await expect(replay.value()).resolves.toBeNull();
  });

  it("should reset and clear state", async () => {
    const replay = new Replay("x");
    await replay.reset();

    expect(replay.key).toBe("");
    await expect(replay.value()).resolves.toBeNull();
  });

  it("should propagate rejection to waiter", async () => {
    const replay = new Replay<string>();

    await expect(replay.resolve(() => Promise.reject(new Error("boom")), "k")).rejects.toThrow(
      "boom",
    );
  });
});
