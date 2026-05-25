import { render, screen } from "@testing-library/react";
import { ComponentProps } from "react";
import { describe, expect, it, vi } from "vitest";
import { ChartData } from "@/helpers/chart.data.ts";
import { OverviewBarChart } from "@/pages/dashboard/overview.bar.chart.tsx";

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
  "overview.chart.user",
  "month",
  (t) => ({
    created: { color: "green", label: t("overview.chart.label.created") },
    deleted: { color: "red", label: t("overview.chart.label.deleted") },
  }),
  [{ created: 3, deleted: 1, month: "January" }],
);

describe("<OverviewBarChart />", () => {
  it("should render chart title from chart data", () => {
    render(<OverviewBarChart chartData={chartData} />);

    expect(screen.getByText("overview.chart.user")).toBeInTheDocument();
  });
});
