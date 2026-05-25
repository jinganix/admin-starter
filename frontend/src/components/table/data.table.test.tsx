import { SortDirection } from "@helpers/paging/pageable.ts";
import { getCoreRowModel, useReactTable, type ColumnDef } from "@tanstack/react-table";
import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { DataTable } from "@/components/table/data.table.tsx";

type Row = { id: string; name: string };

const setPageable = vi.fn();

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({
    pageable: { page: 0, size: 10, sort: {} },
    setPageable,
  }),
}));

function Harness({ rows, loading }: { rows: Row[]; loading: boolean }): React.ReactElement {
  const columns: ColumnDef<Row>[] = [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "name", header: "Name" },
  ];
  const table = useReactTable({
    columns,
    data: rows,
    enableSorting: true,
    getCoreRowModel: getCoreRowModel(),
    state: { sorting: [{ desc: true, id: "name" }] },
  });

  return <DataTable table={table} loading={loading} />;
}

describe("<DataTable />", () => {
  it("should render rows when data is available", () => {
    render(<Harness rows={[{ id: "1", name: "Alice" }]} loading={false} />);

    expect(screen.getByText("Alice")).toBeInTheDocument();
  });

  it("should render empty message when there are no rows", () => {
    render(<Harness rows={[]} loading={false} />);

    expect(screen.getByText("table.empty")).toBeInTheDocument();
  });

  it("should sync sort state to pageable when sorting changes", () => {
    render(<Harness rows={[{ id: "1", name: "Alice" }]} loading={false} />);

    expect(setPageable).toHaveBeenCalledWith(
      expect.objectContaining({
        sort: { name: SortDirection.desc },
      }),
    );
  });
});
