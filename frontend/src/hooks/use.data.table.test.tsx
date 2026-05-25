import { SortDirection } from "@proto/PageableProto.ts";
import { ColumnDef } from "@tanstack/react-table";
import { renderHook } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { useDataTable } from "@/hooks/use.data.table.tsx";

type Row = { id: string; name: string };

const columns: ColumnDef<Row>[] = [
  { accessorKey: "id", header: "ID" },
  { accessorKey: "name", header: "Name" },
];

const data: Row[] = [
  { id: "1", name: "alpha" },
  { id: "2", name: "beta" },
];

describe("useDataTable", () => {
  it("should expose row model for provided data", () => {
    const { result } = renderHook(() => useDataTable({ columns, data }));

    expect(result.current.getRowModel().rows).toHaveLength(2);
    expect(result.current.getRowModel().rows[0]?.original.name).toBe("alpha");
  });

  it("should initialize pagination from pageable when provided", () => {
    const { result } = renderHook(() =>
      useDataTable({
        columns,
        data,
        pageable: { page: 2, size: 25, sort: {} },
      }),
    );

    expect(result.current.getState().pagination.pageIndex).toBe(2);
    expect(result.current.getState().pagination.pageSize).toBe(25);
  });

  it("should initialize sorting from pageable sort directions", () => {
    const { result } = renderHook(() =>
      useDataTable({
        columns,
        data,
        pageable: {
          page: 0,
          size: 10,
          sort: { id: SortDirection.asc, name: SortDirection.desc },
        },
      }),
    );

    expect(result.current.getState().sorting).toEqual(
      expect.arrayContaining([
        { desc: true, id: "name" },
        { desc: false, id: "id" },
      ]),
    );
  });

  it("should use default pageable when pageable is omitted", () => {
    const { result } = renderHook(() => useDataTable({ columns, data }));

    expect(result.current.getState().pagination.pageIndex).toBe(0);
    expect(result.current.getState().pagination.pageSize).toBe(10);
  });
});
