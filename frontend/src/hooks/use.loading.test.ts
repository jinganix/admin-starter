import { act, renderHook, waitFor } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { useLoading } from "@/hooks/use.loading.ts";

describe("useLoading", () => {
  it("should set loading true while async function runs", async () => {
    let resolveFn: (value: string) => void = () => undefined;
    const fn = (): Promise<string> =>
      new Promise<string>((resolve) => {
        resolveFn = resolve;
      });
    const { result } = renderHook(() => useLoading(fn));

    expect(result.current[0]).toBe(false);

    let pending: Promise<string>;
    act(() => {
      pending = result.current[1]();
    });
    expect(result.current[0]).toBe(true);

    act(() => resolveFn("done"));
    await waitFor(() => expect(result.current[0]).toBe(false));
    await expect(pending!).resolves.toBe("done");
  });

  it("should clear loading when async function throws", async () => {
    const fn = (): Promise<never> => Promise.reject(new Error("fail"));
    const { result } = renderHook(() => useLoading(fn));

    await expect(act(() => result.current[1]())).rejects.toThrow("fail");
    expect(result.current[0]).toBe(false);
  });
});
