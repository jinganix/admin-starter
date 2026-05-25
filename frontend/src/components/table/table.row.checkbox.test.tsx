import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { tableRowCheckbox } from "@/components/table/table.row.checkbox.tsx";

type Row = { id: string };

function renderCheckboxParts(): {
  toggleAllPageRowsSelected: ReturnType<typeof vi.fn>;
  toggleSelected: ReturnType<typeof vi.fn>;
} {
  const toggleSelected = vi.fn();
  const toggleAllPageRowsSelected = vi.fn();
  const row = {
    getIsSelected: () => false,
    toggleSelected,
  };
  const table = {
    getIsAllPageRowsSelected: () => false,
    getIsSomePageRowsSelected: () => false,
    toggleAllPageRowsSelected,
  };
  const column = tableRowCheckbox<Row>();
  const header = column.header;
  const cell = column.cell;

  render(
    <>
      {typeof header === "function" ? header({ table } as never) : null}
      {typeof cell === "function" ? cell({ row } as never) : null}
    </>,
  );

  return { toggleAllPageRowsSelected, toggleSelected };
}

describe("tableRowCheckbox", () => {
  it("should render select-all and row checkboxes when mounted", () => {
    renderCheckboxParts();

    expect(screen.getByLabelText("Select all")).toBeInTheDocument();
    expect(screen.getByLabelText("Select row")).toBeInTheDocument();
  });

  it("should toggle row selection when user clicks row checkbox", async () => {
    const { toggleSelected } = renderCheckboxParts();

    await userEvent.click(screen.getByLabelText("Select row"));

    expect(toggleSelected).toHaveBeenCalledWith(true);
  });

  it("should toggle all rows when user clicks header checkbox", async () => {
    const { toggleAllPageRowsSelected } = renderCheckboxParts();

    await userEvent.click(screen.getByLabelText("Select all"));

    expect(toggleAllPageRowsSelected).toHaveBeenCalledWith(true);
  });
});
