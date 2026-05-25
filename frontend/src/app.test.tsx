import { render, screen, waitFor } from "@testing-library/react";
import { container } from "tsyringe";
import { afterEach, describe, expect, it, vi } from "vitest";
import { App } from "@/app.tsx";
import * as useLoading from "@/hooks/use.loading.ts";
import * as storeContext from "@/sys/store.context.tsx";

vi.mock("@/components/routes/app.router.tsx", () => ({
  AppRouter: () => <nav aria-label="app routes" />,
}));

vi.mock("@/components/ui/toaster.tsx", () => ({
  Toaster: () => null,
}));

describe("<App />", () => {
  afterEach(() => vi.restoreAllMocks());

  it("should show loading spinner when app is loading", () => {
    vi.spyOn(useLoading, "useLoading").mockReturnValue([true, vi.fn()]);
    vi.spyOn(storeContext, "useAuthStore").mockReturnValue({
      initialize: vi.fn(),
    } as never);

    const { container } = render(<App />);

    expect(container.querySelector(".animate-spin")).toBeInTheDocument();
    expect(screen.queryByRole("navigation", { name: "app routes" })).not.toBeInTheDocument();
  });

  it("should show routes when app is not loading", () => {
    vi.spyOn(useLoading, "useLoading").mockReturnValue([false, vi.fn()]);
    vi.spyOn(storeContext, "useAuthStore").mockReturnValue({
      initialize: vi.fn(),
    } as never);

    render(<App />);

    expect(screen.getByRole("navigation", { name: "app routes" })).toBeInTheDocument();
    expect(document.querySelector(".animate-spin")).not.toBeInTheDocument();
  });

  it("should initialize auth on mount", async () => {
    const deleteToken = vi.fn().mockResolvedValue(undefined);
    vi.spyOn(container, "resolve").mockReturnValue({ deleteToken } as never);
    const initialize = vi.fn(async (onError?: () => Promise<void>) => {
      await onError?.();
    });
    vi.spyOn(storeContext, "useAuthStore").mockReturnValue({ initialize } as never);

    render(<App />);

    await waitFor(() => expect(initialize).toHaveBeenCalledOnce());
    await waitFor(() => expect(deleteToken).toHaveBeenCalledOnce());
    expect(screen.getByRole("navigation", { name: "app routes" })).toBeInTheDocument();
  });
});
