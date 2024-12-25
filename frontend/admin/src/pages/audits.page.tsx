import { Paging } from "@helpers/paging/paging.ts";
import { defaultSearchParams, toPageable, toSearchParams } from "@helpers/search.params.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import { observer } from "mobx-react-lite";
import { FC, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { formSchema, FormValues } from "./audit/audit.filter.form";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { TableTitle } from "@/components/layout/table.title.tsx";
import { DataTable } from "@/components/table/data.table.tsx";
import { TableColumnHeader } from "@/components/table/table.column.header.tsx";
import { TableFooter } from "@/components/table/table.footer.tsx";
import { TableParamsProvider, useTableParams } from "@/components/table/table.params.context.tsx";
import { TableViewOptions } from "@/components/table/table.view.options.tsx";
import { useDataTable } from "@/hooks/use.data.table.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { valuesResolver } from "@/pages/audit/audit.filter.form.tsx";
import { AuditTableFilters } from "@/pages/audit/audit.table.filters.tsx";
import { Audit } from "@/sys/audit/audit.ts";
import { auditsStore } from "@/sys/audit/audits.store";

const i18nKey = (id: string): string => `audit.header.${id}`;

export const columns = (): ColumnDef<Audit>[] => [
  {
    accessorKey: "id",
    cell: ({ row }) => <div className="leading-6">{row.original.id}</div>,
    header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
  },
  {
    accessorKey: "username",
    cell: ({ row }) => <div>{row.original.username}</div>,
    header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
  },
  {
    accessorKey: "path",
    cell: ({ row }) => <div>{row.original.path}</div>,
    header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
  },
  {
    accessorKey: "method",
    cell: ({ row }) => <div>{row.original.method}</div>,
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

export const AuditsComponent: FC = observer(() => {
  const { t } = useTranslation();
  const [params, setParams] = useTableParams();

  const form = useForm<FormValues>({
    defaultValues: valuesResolver.resolve(),
    resolver: zodResolver(formSchema),
    values: valuesResolver.resolve(params),
  });

  const pageable = toPageable(params);
  const [loading, loadData] = useLoading(async (): Promise<void> => {
    const data = form.getValues();
    setParams(toSearchParams({ ...pageable, ...data }));
    params.size && (await auditsStore.load(pageable, data.username, data.method, data.path));
  }, false);

  useEffect(() => void loadData(), [params]);
  const { paging, records } = auditsStore;
  const table = useDataTable({ columns: columns(), data: records, pageable });

  return (
    <div className="p-4 md:px-8 space-y-2 md:space-y-4 h-full flex flex-col">
      <TableTitle title={t("audit.title.")} sub={t("audit.title.sub")} />

      <div className="flex items-center justify-end xl:justify-between space-x-4">
        <AuditTableFilters form={form} loadData={loadData} />

        <div className="flex items-center gap-4">
          <TableViewOptions i18nKey={i18nKey} table={table} />
        </div>
      </div>

      <DataTable table={table} loading={loading} />

      <TableFooter paging={paging || new Paging(pageable.size)} />
    </div>
  );
});

export const AuditsPage: FC = () => {
  return (
    <LayoutContent fixed>
      <TableParamsProvider value={defaultSearchParams()}>
        <AuditsComponent />
      </TableParamsProvider>
    </LayoutContent>
  );
};
