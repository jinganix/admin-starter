import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TableFilterableCell } from "@/components/table/table.filterable.cell.tsx";

describe("<TableFilterableCell />", () => {
  it("should render children when mounted", () => {
    render(<TableFilterableCell>filter value</TableFilterableCell>);

    expect(screen.getByText("filter value")).toBeInTheDocument();
  });

  it("should forward click handler when provided", async () => {
    const onClick = vi.fn();
    render(<TableFilterableCell onClick={onClick}>click me</TableFilterableCell>);

    await userEvent.click(screen.getByText("click me"));

    expect(onClick).toHaveBeenCalledOnce();
  });
});
