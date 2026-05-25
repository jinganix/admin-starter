import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { ChartData } from "@/helpers/chart.data.ts";
import { DashboardPage } from "@/pages/dashboard.page.tsx";

const load = vi.fn().mockResolvedValue(undefined);

vi.mock("framer-motion", () => ({
  motion: {
    div: "div",
  },
}));

vi.mock("@/sys/store.context.tsx", () => ({
  useOverviewsStore: () => ({
    apiData: new ChartData("overview.chart.apiCalled", "month", () => ({}), []),
    apiGet: 10,
    apiPost: 5,
    load,
    permissionCreated: 1,
    permissionData: new ChartData("overview.chart.permission", "month", () => ({}), []),
    roleCreated: 2,
    roleData: new ChartData("overview.chart.role", "month", () => ({}), []),
    userCreated: 3,
    userData: new ChartData("overview.chart.user", "month", () => ({}), []),
  }),
}));

vi.mock("@/pages/dashboard/overview.area.chart.tsx", () => ({
  OverviewAreaChart: () => <div>area chart</div>,
}));
vi.mock("@/pages/dashboard/overview.bar.chart.tsx", () => ({
  OverviewBarChart: () => <div>bar chart</div>,
}));
vi.mock("@/pages/dashboard/overview.line.chart.tsx", () => ({
  OverviewLineChat: () => <div>line chart</div>,
}));

describe("<DashboardPage />", () => {
  it("should load overview data and render summary cards", () => {
    render(<DashboardPage />);

    expect(load).toHaveBeenCalled();
    expect(screen.getByText("overview.apiCalled")).toBeInTheDocument();
    expect(screen.getByText("15")).toBeInTheDocument();
    expect(screen.getByText("overview.userCreated")).toBeInTheDocument();
    expect(screen.getByText("3")).toBeInTheDocument();
    expect(screen.getAllByText("line chart")).toHaveLength(2);
    expect(screen.getByText("area chart")).toBeInTheDocument();
    expect(screen.getByText("bar chart")).toBeInTheDocument();
  });
});
