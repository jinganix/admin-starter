import { act, render, renderHook, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { TreeStateProvider, useTreeState } from "@/components/tree/use.tree.state.tsx";

describe("useTreeState", () => {
  it("should throw when used outside provider", () => {
    expect(() => renderHook(() => useTreeState())).toThrow(
      "useTreeState must be used within a TreeStateProvider",
    );
  });

  it("should provide default partially state when mounted", () => {
    const { result } = renderHook(() => useTreeState(), {
      wrapper: ({ children }) => <TreeStateProvider>{children}</TreeStateProvider>,
    });

    expect(result.current[0]).toBe("partially");
  });

  it("should update state when setState is called", () => {
    const { result } = renderHook(() => useTreeState(), {
      wrapper: ({ children }) => <TreeStateProvider>{children}</TreeStateProvider>,
    });

    act(() => result.current[1]("expanded"));

    expect(result.current[0]).toBe("expanded");
  });
});

describe("<TreeStateProvider />", () => {
  it("should render children when mounted", () => {
    render(
      <TreeStateProvider state="collapsed">
        <span>tree-child</span>
      </TreeStateProvider>,
    );

    expect(screen.getByText("tree-child")).toBeInTheDocument();
  });
});
