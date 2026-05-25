import { render, screen } from "@testing-library/react";
import { ComponentProps } from "react";
import { describe, expect, it, vi } from "vitest";
import { ChartData } from "@/helpers/chart.data.ts";
import { OverviewAreaChart } from "@/pages/dashboard/overview.area.chart.tsx";

vi.mock("recharts", async (importOriginal) => {
  const actual = await importOriginal<typeof import("recharts")>();
  return {
    ...actual,
    XAxis: (props: ComponentProps<typeof actual.XAxis>) => {
      expect(props.tickFormatter?.("January", 0)).toBe("Jan");
      return <actual.XAxis {...props} />;
    },
  };
});

const chartData = new ChartData(
  "overview.chart.apiCalled",
  "month",
  (t) => ({
    get: { color: "red", label: t("overview.chart.label.get") },
    post: { color: "blue", label: t("overview.chart.label.post") },
  }),
  [{ get: 1, month: "January", post: 2 }],
);

describe("<OverviewAreaChart />", () => {
  it("should render chart title from chart data", () => {
    render(<OverviewAreaChart chartData={chartData} />);

    expect(screen.getByText("overview.chart.apiCalled")).toBeInTheDocument();
  });
});
