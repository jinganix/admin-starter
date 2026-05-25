import { emitter } from "@helpers/event/emitter.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import { afterEach, describe, expect, it, vi } from "vitest";

const navigate = vi.fn();
const satisfy = vi.fn();

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy }),
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigate,
  };
});

vi.mock("@/components/routes/routes.tsx", async () => {
  const { Outlet } = await import("react-router");
  const { CondType } = await import("@helpers/condition/cond.types.ts");

  return {
    ROUTES: [
      {
        cond: { type: CondType.always },
        element: <div>guarded-route</div>,
        path: "/guarded",
      },
      {
        element: (
          <div>
            layout-route
            <Outlet />
          </div>
        ),
        path: "/layout",
        routes: [{ element: <div>nested-route</div>, path: "nested" }],
      },
      {
        cond: { type: CondType.always },
        element: (
          <div>
            cond-layout
            <Outlet />
          </div>
        ),
        path: "/cond-layout",
        routes: [
          {
            cond: { type: CondType.always },
            element: <div>cond-child</div>,
            path: "child",
          },
        ],
      },
      { element: <div>plain-route</div>, path: "/plain" },
    ],
  };
});

import { AppRoutes } from "@/components/routes/app.routes.tsx";

describe("<AppRoutes />", () => {
  afterEach(() => {
    navigate.mockClear();
    satisfy.mockReset();
    satisfy.mockReturnValue(true);
  });

  it.each([ErrorCode.ACCESS_DENIED, ErrorCode.USER_IS_INACTIVE, ErrorCode.PERMISSION_DENIED])(
    "should navigate to forbidden page when error code %s is emitted",
    (code) => {
      render(
        <MemoryRouter>
          <AppRoutes />
        </MemoryRouter>,
      );

      emitter.emit("error", code);

      expect(navigate).toHaveBeenCalledWith("/403");
    },
  );

  it("should ignore unrelated error codes", () => {
    render(
      <MemoryRouter>
        <AppRoutes />
      </MemoryRouter>,
    );

    emitter.emit("error", ErrorCode.BAD_TOKEN);

    expect(navigate).not.toHaveBeenCalled();
  });

  it("should render guarded route when cond route matches", () => {
    render(
      <MemoryRouter initialEntries={["/guarded"]}>
        <AppRoutes />
      </MemoryRouter>,
    );

    expect(screen.getByText("guarded-route")).toBeInTheDocument();
  });

  it("should render nested routes from layout route", () => {
    render(
      <MemoryRouter initialEntries={["/layout/nested"]}>
        <AppRoutes />
      </MemoryRouter>,
    );

    expect(screen.getByText("nested-route")).toBeInTheDocument();
  });

  it("should render conditional nested route", () => {
    render(
      <MemoryRouter initialEntries={["/cond-layout/child"]}>
        <AppRoutes />
      </MemoryRouter>,
    );

    expect(screen.getByText("cond-child")).toBeInTheDocument();
  });
});
