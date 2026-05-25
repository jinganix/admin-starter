import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { CondRoute } from "@/components/condition/cond.route.tsx";
import { CondType } from "@/helpers/condition/cond.types.ts";

const satisfy = vi.fn();

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy }),
}));

vi.mock("react-router-dom", async () => {
  const React = await import("react");
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return {
    ...actual,
    Navigate: ({ to }: { to: string }) =>
      React.createElement("div", { "data-testid": "redirect" }, to),
  };
});

describe("<CondRoute />", () => {
  beforeEach(() => {
    satisfy.mockReset();
  });

  afterEach(() => vi.clearAllMocks());

  it("should render element without checking cond when pathname differs from route path", () => {
    satisfy.mockReturnValue(false);

    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route
            path="/dashboard"
            element={
              <CondRoute
                cond={{ type: CondType.never }}
                element={<p>protected</p>}
                path="/"
                redirects={[{ cond: { type: CondType.always }, path: "/login" }]}
              />
            }
          />
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByText("protected")).toBeInTheDocument();
    expect(satisfy).not.toHaveBeenCalled();
  });

  it("should render element when cond is satisfied on matching path", () => {
    satisfy.mockReturnValue(true);

    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route
            path="/dashboard"
            element={
              <CondRoute
                cond={{ type: CondType.authed }}
                element={<p>protected</p>}
                path="/dashboard"
              />
            }
          />
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByText("protected")).toBeInTheDocument();
    expect(satisfy).toHaveBeenCalledWith({ type: CondType.authed });
  });

  it("should redirect to first matching redirect when cond fails", () => {
    satisfy.mockImplementation((cond) => cond?.type === CondType.always);

    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route
            path="/dashboard"
            element={
              <CondRoute
                cond={{ type: CondType.never }}
                element={<p>protected</p>}
                path="/dashboard"
                redirects={[{ cond: { type: CondType.always }, path: "/login" }]}
              />
            }
          />
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByTestId("redirect")).toHaveTextContent("/login");
    expect(screen.queryByText("protected")).not.toBeInTheDocument();
  });

  it("should redirect to forbidden page when cond and redirects fail", () => {
    satisfy.mockReturnValue(false);

    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route
            path="/dashboard"
            element={
              <CondRoute
                cond={{ type: CondType.never }}
                element={<p>protected</p>}
                path="/dashboard"
                redirects={[{ cond: { type: CondType.never }, path: "/login" }]}
              />
            }
          />
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByTestId("redirect")).toHaveTextContent("/403");
    expect(screen.queryByText("protected")).not.toBeInTheDocument();
  });
});
