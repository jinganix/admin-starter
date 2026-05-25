import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TableViewOptions } from "@/components/table/table.view.options.tsx";

function createTable(): {
  columns: { id: string }[];
  getAllColumns: () => {
    getCanHide: () => boolean;
    id: string;
    toggleVisibility: ReturnType<typeof vi.fn>;
  }[];
  idColumn: { id: string; toggleVisibility: ReturnType<typeof vi.fn> };
  nameColumn: { id: string; toggleVisibility: ReturnType<typeof vi.fn> };
} {
  const nameColumn = {
    accessorFn: () => "name",
    getCanHide: () => true,
    getIsVisible: () => true,
    id: "name",
    toggleVisibility: vi.fn(),
  };
  const idColumn = {
    accessorFn: () => "id",
    getCanHide: () => true,
    getIsVisible: () => false,
    id: "id",
    toggleVisibility: vi.fn(),
  };
  const selectColumn = {
    getCanHide: () => false,
    id: "select",
    toggleVisibility: vi.fn(),
  };

  return {
    columns: [nameColumn, idColumn, selectColumn],
    getAllColumns: () => [nameColumn, idColumn, selectColumn],
    idColumn,
    nameColumn,
  };
}

describe("<TableViewOptions />", () => {
  it("should render view options trigger when mounted", () => {
    const table = createTable();

    render(<TableViewOptions table={table as never} i18nKey={(id) => `table.column.${id}`} />);

    expect(screen.getByRole("button")).toBeInTheDocument();
  });

  it("should list hideable columns when menu is opened", async () => {
    const table = createTable();

    render(<TableViewOptions table={table as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByText("table.view.header")).toBeInTheDocument();
    expect(screen.getByText("table.column.name")).toBeInTheDocument();
    expect(screen.getByText("table.column.id")).toBeInTheDocument();
    expect(screen.queryByText("table.column.select")).not.toBeInTheDocument();
  });

  it("should toggle column visibility when user checks an option", async () => {
    const table = createTable();

    render(<TableViewOptions table={table as never} i18nKey={(id) => `table.column.${id}`} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("table.column.id"));

    expect(table.idColumn.toggleVisibility).toHaveBeenCalled();
  });
});
