import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { FacetedFilter } from "@/components/ui/faceted.filter.tsx";

vi.mock("@/hooks/use.mobile.tsx", () => ({
  useIsMobile: () => false,
}));

describe("<FacetedFilter />", () => {
  const setSelected = vi.fn();
  const options = [
    { label: "Active", value: "active" },
    { label: "Inactive", value: "inactive" },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    Element.prototype.scrollIntoView = vi.fn();
  });

  it("should render filter title when mounted", () => {
    render(
      <FacetedFilter title="Status" options={options} selected={[]} setSelected={setSelected} />,
    );

    expect(screen.getByRole("button", { name: /Status/i })).toBeInTheDocument();
  });

  it("should show selected badges when values are chosen", () => {
    render(
      <FacetedFilter
        title="Status"
        options={options}
        selected={["active"]}
        setSelected={setSelected}
      />,
    );

    expect(screen.getByText("Active")).toBeInTheDocument();
  });

  it("should show count badge when many values are selected", () => {
    render(
      <FacetedFilter
        title="Status"
        options={options}
        selected={["active", "inactive", "pending"]}
        setSelected={setSelected}
        maxShowed={2}
      />,
    );

    expect(screen.getByText("3 selected")).toBeInTheDocument();
  });

  it("should update selection when user picks an option", async () => {
    render(
      <FacetedFilter title="Status" options={options} selected={[]} setSelected={setSelected} />,
    );

    await userEvent.click(screen.getByRole("button", { name: /Status/i }));
    await userEvent.click(screen.getByText("Active"));

    expect(setSelected).toHaveBeenCalledWith(["active"]);
  });

  it("should clear selection when user chooses clear filters", async () => {
    render(
      <FacetedFilter
        title="Status"
        options={options}
        selected={["active"]}
        setSelected={setSelected}
      />,
    );

    await userEvent.click(screen.getByRole("button", { name: /Status/i }));
    await userEvent.click(screen.getByText("action.clearFilters"));

    expect(setSelected).toHaveBeenCalledWith(undefined);
  });
});
