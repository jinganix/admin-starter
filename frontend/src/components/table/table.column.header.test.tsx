import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TableColumnHeader } from "@/components/table/table.column.header.tsx";

function createColumn(
  overrides: Partial<{
    id: string;
    canSort: boolean;
    sorted: false | "asc" | "desc";
    canMultiSort: boolean;
  }> = {},
): {
  clearSorting: ReturnType<typeof vi.fn>;
  getCanMultiSort: () => boolean;
  getCanSort: () => boolean;
  getIsSorted: () => false | "asc" | "desc";
  id: string;
  toggleSorting: ReturnType<typeof vi.fn>;
  toggleVisibility: ReturnType<typeof vi.fn>;
} {
  const { id = "name", canSort = true, sorted = false, canMultiSort = false } = overrides;

  return {
    clearSorting: vi.fn(),
    getCanMultiSort: () => canMultiSort,
    getCanSort: () => canSort,
    getIsSorted: () => sorted,
    id,
    toggleSorting: vi.fn(),
    toggleVisibility: vi.fn(),
  };
}

describe("<TableColumnHeader />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render plain title when column is not sortable", () => {
    render(
      <TableColumnHeader
        column={createColumn({ canSort: false }) as never}
        i18nKey={(id) => `table.column.${id}`}
      />,
    );

    expect(screen.getByText("table.column.name")).toBeInTheDocument();
    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });

  it("should render sort menu when column is sortable", async () => {
    const column = createColumn({ sorted: "asc" });

    render(<TableColumnHeader column={column as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByText("table.header.menu.asc")).toBeInTheDocument();
    expect(screen.getByText("table.header.menu.desc")).toBeInTheDocument();
    expect(screen.getByText("table.header.menu.unsorted")).toBeInTheDocument();
    expect(screen.getByText("table.header.menu.hide")).toBeInTheDocument();
  });

  it("should toggle sorting when user selects sort option", async () => {
    const column = createColumn();

    render(<TableColumnHeader column={column as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("table.header.menu.asc"));

    expect(column.toggleSorting).toHaveBeenCalledWith(false, false);
  });

  it("should toggle descending sort when user selects desc option", async () => {
    const column = createColumn();

    render(<TableColumnHeader column={column as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("table.header.menu.desc"));

    expect(column.toggleSorting).toHaveBeenCalledWith(true, false);
  });

  it("should clear sorting when user selects unsorted option", async () => {
    const column = createColumn({ sorted: "desc" });

    render(<TableColumnHeader column={column as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("table.header.menu.unsorted"));

    expect(column.clearSorting).toHaveBeenCalledOnce();
  });

  it("should hide column when user selects hide option", async () => {
    const column = createColumn();

    render(<TableColumnHeader column={column as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("table.header.menu.hide"));

    expect(column.toggleVisibility).toHaveBeenCalledWith(false);
  });
});
