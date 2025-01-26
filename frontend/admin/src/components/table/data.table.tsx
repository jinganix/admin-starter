import { cn } from "@helpers/lib/cn.ts";
import { SortDirection } from "@proto/PageableProto.ts";
import { flexRender, Table as TableDef } from "@tanstack/react-table";
import { keyBy, mapValues } from "lodash";
import { HTMLAttributes, ReactNode, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/shadcn/table";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";

interface Props<TData> extends HTMLAttributes<HTMLDivElement> {
  table: TableDef<TData>;
  loading: boolean;
}

export function DataTable<TData>({ table, loading, className }: Props<TData>): ReactNode {
  const { t } = useTranslation();
  const { pageable, setPageable } = useTableData();
  const setSort = (sort: Record<string, SortDirection>) => void setPageable({ ...pageable, sort });

  const ref = useRef<HTMLDivElement>(null);
  useEffect(() => void (loading && ref.current?.scrollTo(0, 0)), [loading]);

  const { sorting } = table.getState();
  useEffect(() => {
    const sorts = sorting
      .filter(({ id }) => Boolean(id))
      .map(({ id, desc }) => ({
        direction: desc ? SortDirection.desc : SortDirection.asc,
        name: id,
      }));
    setSort(mapValues(keyBy(sorts, "name"), "direction"));
  }, [sorting]);

  return (
    <div
      ref={ref}
      className={cn(
        "relative rounded-md border",
        loading ? "overflow-hidden" : "overflow-auto",
        className,
      )}
    >
      <Spinner className="bg-secondary/50" loading={loading} size={30} />
      <Table>
        <TableHeader>
          {table.getHeaderGroups().map((headerGroup) => (
            <TableRow key={headerGroup.id}>
              {headerGroup.headers.map((header) => {
                return (
                  <TableHead className="px-3 md:px-4" key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
                  </TableHead>
                );
              })}
            </TableRow>
          ))}
        </TableHeader>
        <TableBody>
          {table.getRowModel().rows?.length ? (
            table.getRowModel().rows.map((row) => (
              <TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
                {row.getVisibleCells().map((cell) => (
                  <TableCell className="text-center p-3 md:p-4" key={cell.id}>
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </TableCell>
                ))}
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={table.getAllColumns().length} className="text-center p-2 md:p-3">
                {t("table.empty")}
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </div>
  );
}
