import { act, renderHook } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { useIsMobile } from "@/hooks/use-mobile.tsx";

function mockMatchMedia(width: number): { setWidth(nextWidth: number): void } {
  let changeListener: (() => void) | null = null;

  Object.defineProperty(window, "innerWidth", {
    configurable: true,
    value: width,
    writable: true,
  });

  window.matchMedia = vi.fn().mockImplementation((query) => ({
    addEventListener: (_event: string, listener: () => void) => {
      changeListener = listener;
    },
    dispatchEvent: vi.fn(),
    matches: width < 768,
    media: query,
    onchange: null,
    removeEventListener: () => {
      changeListener = null;
    },
  }));

  return {
    setWidth(nextWidth: number) {
      Object.defineProperty(window, "innerWidth", {
        configurable: true,
        value: nextWidth,
        writable: true,
      });
      act(() => changeListener?.());
    },
  };
}

describe("useIsMobile", () => {
  afterEach(() => vi.restoreAllMocks());

  it("should return false when viewport width is at desktop breakpoint", () => {
    mockMatchMedia(1024);

    const { result } = renderHook(() => useIsMobile());

    expect(result.current).toBe(false);
  });

  it("should return true when viewport width is below mobile breakpoint", () => {
    mockMatchMedia(500);

    const { result } = renderHook(() => useIsMobile());

    expect(result.current).toBe(true);
  });

  it("should update when matchMedia change event fires", () => {
    const media = mockMatchMedia(1024);
    const { result } = renderHook(() => useIsMobile());

    expect(result.current).toBe(false);

    media.setWidth(500);

    expect(result.current).toBe(true);
  });
});
