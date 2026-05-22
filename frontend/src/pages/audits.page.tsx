import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { toPageable } from "@helpers/search.params.ts";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { TableTitle } from "@/components/layout/table.title.tsx";
import { DataTable } from "@/components/table/data.table.tsx";
import { TableColumnHeader } from "@/components/table/table.column.header.tsx";
import { TableDataProvider, useTableData } from "@/components/table/table.data.context.tsx";
import { TableFilterableCell } from "@/components/table/table.filterable.cell.tsx";
import { TableFooter } from "@/components/table/table.footer.tsx";
import { TableViewOptions } from "@/components/table/table.view.options.tsx";
import { useDataTable } from "@/hooks/use.data.table.tsx";
import { valuesResolver } from "@/pages/audit/audit.filter.form.schema.tsx";
import { AuditFilterForm } from "@/pages/audit/audit.filter.form.tsx";
import { AuditActions } from "@/sys/audit/audit.actions.ts";
import { Audit, AuditQuery } from "@/sys/audit/audit.types.ts";

const i18nKey = (id: string): string => `audit.header.${id}`;

export const AuditsComponent: FC = () => {
  const { t } = useTranslation();
  const { loading, pageable, records, query, setQuery } = useTableData<AuditQuery, Audit>();

  const columns: ColumnDef<Audit>[] = [
    {
      accessorKey: "id",
      cell: ({ row }) => <div className="leading-6">{row.original.id}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "userId",
      cell: ({ row }) => (
        <TableFilterableCell onClick={() => setQuery({ ...query, userId: row.original.userId })}>
          {row.original.userId}
        </TableFilterableCell>
      ),
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "username",
      cell: ({ row }) => (
        <>
          {row.original.username && (
            <TableFilterableCell
              onClick={() => setQuery({ ...query, username: row.original.username })}
            >
              {row.original.username}
            </TableFilterableCell>
          )}
          {!row.original.username && <div className="text-red-400">[{t("audit.deleted")}]</div>}
        </>
      ),
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "path",
      cell: ({ row }) => (
        <TableFilterableCell onClick={() => setQuery({ ...query, path: row.original.path })}>
          {row.original.path}
        </TableFilterableCell>
      ),
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "method",
      cell: ({ row }) => (
        <TableFilterableCell onClick={() => setQuery({ ...query, method: row.original.method })}>
          {row.original.method}
        </TableFilterableCell>
      ),
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "createdAt",
      cell: ({ row }) => (
        <div className="whitespace-nowrap">{dayjs(row.original.createdAt).format()}</div>
      ),
      enableMultiSort: true,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
  ];

  const table = useDataTable({ columns, data: records, pageable });

  return (
    <div className="p-4 md:px-8 space-y-2 md:space-y-4 h-full flex flex-col">
      <TableTitle title={t("audit.title.")} sub={t("audit.title.sub")} />

      <div className="flex items-center justify-end xl:justify-between space-x-4">
        <AuditFilterForm />

        <div className="flex items-center gap-4">
          <TableViewOptions i18nKey={i18nKey} table={table} />
        </div>
      </div>

      <DataTable table={table} loading={loading} />

      <TableFooter />
    </div>
  );
};

export const AuditsPage: FC = () => {
  const params = new URLSearchParams(window.location.search);

  return (
    <LayoutContent fixed>
      <TableDataProvider
        loadData={AuditActions.list}
        pageable={params.size ? toPageable(params) : DEFAULT_PAGEABLE}
        query={params.size ? valuesResolver.resolve(params) : {}}
      >
        <AuditsComponent />
      </TableDataProvider>
    </LayoutContent>
  );
};
