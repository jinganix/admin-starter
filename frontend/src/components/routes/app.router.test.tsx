import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { AppRouter } from "@/components/routes/app.router.tsx";

vi.mock("@/components/routes/app.routes.tsx", () => ({
  AppRoutes: () => <div>app-routes</div>,
}));

vi.mock("react-router", async () => {
  const actual = await vi.importActual<typeof import("react-router")>("react-router");
  return {
    ...actual,
    BrowserRouter: ({ children }: { children: React.ReactNode }) => (
      <div data-testid="browser-router">{children}</div>
    ),
  };
});

describe("<AppRouter />", () => {
  it("should render app routes inside browser router when mounted", () => {
    render(<AppRouter />);

    expect(screen.getByTestId("browser-router")).toBeInTheDocument();
    expect(screen.getByText("app-routes")).toBeInTheDocument();
  });
});
