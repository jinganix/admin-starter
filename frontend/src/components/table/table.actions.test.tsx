import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TableActions } from "@/components/table/table.actions.tsx";

const useMediaQuery = vi.fn();

vi.mock("react-responsive", () => ({
  useMediaQuery: () => useMediaQuery(),
}));

describe("<TableActions />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render inline actions on xl screens", () => {
    useMediaQuery.mockReturnValue(true);

    render(
      <TableActions>
        <button type="button">create</button>
      </TableActions>,
    );

    expect(screen.getByRole("button", { name: "create" })).toBeInTheDocument();
  });

  it("should render dropdown menu on smaller screens", async () => {
    useMediaQuery.mockReturnValue(false);

    render(
      <TableActions>
        <button type="button">create</button>
      </TableActions>,
    );

    await userEvent.click(screen.getAllByRole("button")[0]);

    expect(screen.getByText("table.action.header")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "create" })).toBeInTheDocument();
  });
});
