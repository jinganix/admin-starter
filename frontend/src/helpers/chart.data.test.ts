import { describe, expect, it } from "vitest";
import { ChartData, formatMonthTick } from "@/helpers/chart.data.ts";

describe("chart.data", () => {
  it("should shorten month labels to three characters", () => {
    expect(formatMonthTick("January")).toBe("Jan");
  });

  it("should store chart metadata and records", () => {
    const data = new ChartData("title.key", "month", () => ({}), [{ month: "Jan" }]);

    expect(data.title).toBe("title.key");
    expect(data.xKey).toBe("month");
    expect(data.records).toEqual([{ month: "Jan" }]);
  });
});
