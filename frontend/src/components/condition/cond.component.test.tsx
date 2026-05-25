import { render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { CondComponent } from "@/components/condition/cond.component.tsx";
import { CondType } from "@/helpers/condition/cond.types.ts";

const satisfy = vi.fn();

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy }),
}));

describe("<CondComponent />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render children when cond is satisfied", () => {
    satisfy.mockReturnValue(true);

    render(
      <CondComponent cond={{ type: CondType.authed }}>
        <span>visible content</span>
      </CondComponent>,
    );

    expect(screen.getByText("visible content")).toBeInTheDocument();
    expect(satisfy).toHaveBeenCalledWith({ type: CondType.authed });
  });

  it("should render nothing when cond is not satisfied", () => {
    satisfy.mockReturnValue(false);

    render(
      <CondComponent cond={{ type: CondType.authed }}>
        <span>hidden content</span>
      </CondComponent>,
    );

    expect(screen.queryByText("hidden content")).not.toBeInTheDocument();
    expect(satisfy).toHaveBeenCalledWith({ type: CondType.authed });
  });
});
