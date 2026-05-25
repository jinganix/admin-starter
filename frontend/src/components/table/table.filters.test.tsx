import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TableFilters } from "@/components/table/table.filters.tsx";

const useMediaQuery = vi.fn();

vi.mock("react-responsive", () => ({
  useMediaQuery: () => useMediaQuery(),
}));

describe("<TableFilters />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render filters inline on xl screens", () => {
    useMediaQuery.mockReturnValue(true);

    render(
      <TableFilters>
        <input aria-label="name-filter" />
      </TableFilters>,
    );

    expect(screen.getByLabelText("name-filter")).toBeInTheDocument();
  });

  it("should render filters in dropdown on smaller screens", async () => {
    useMediaQuery.mockReturnValue(false);

    render(
      <TableFilters>
        <input aria-label="name-filter" />
      </TableFilters>,
    );

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByText("table.filter.header")).toBeInTheDocument();
    expect(screen.getByLabelText("name-filter")).toBeInTheDocument();
  });
});
