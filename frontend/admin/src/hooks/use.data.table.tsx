import { Pageable } from "@helpers/paging/pageable.ts";
import { SortDirection } from "@proto/PageableProto.ts";
import { ColumnDef, getCoreRowModel, Table, useReactTable } from "@tanstack/react-table";

interface Props<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  pageable?: Pageable;
}

export function useDataTable<TData, TValue>({
  columns,
  data,
  pageable = new Pageable(),
}: Props<TData, TValue>): Table<TData> {
  return useReactTable({
    columns,
    data,
    enableMultiSort: false,
    getCoreRowModel: getCoreRowModel(),
    initialState: {
      pagination: {
        pageIndex: pageable.page,
        pageSize: pageable.size,
      },
      sorting: Object.entries(pageable.sort).map(([id, dir]) => ({
        desc: dir === SortDirection.desc,
        id,
      })),
    },
  });
}
