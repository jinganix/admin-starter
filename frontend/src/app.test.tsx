import { render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { App } from "@/app.tsx";
import * as useLoading from "@/hooks/use.loading.ts";

vi.mock("@/components/routes/app.router.tsx", () => ({
  AppRouter: () => <nav aria-label="app routes" />,
}));

describe("<App />", () => {
  afterEach(() => vi.restoreAllMocks());

  it("should show loading spinner when app is loading", () => {
    vi.spyOn(useLoading, "useLoading").mockReturnValue([true, vi.fn()]);

    const { container } = render(<App />);

    expect(container.querySelector(".animate-spin")).toBeInTheDocument();
    expect(screen.queryByRole("navigation", { name: "app routes" })).not.toBeInTheDocument();
  });

  it("should show routes when app is not loading", () => {
    vi.spyOn(useLoading, "useLoading").mockReturnValue([false, vi.fn()]);

    render(<App />);

    expect(screen.getByRole("navigation", { name: "app routes" })).toBeInTheDocument();
    expect(document.querySelector(".animate-spin")).not.toBeInTheDocument();
  });
});
