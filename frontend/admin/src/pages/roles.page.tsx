import { emitter } from "@helpers/event/emitter.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { toPageable } from "@helpers/search.params.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import { PlusIcon } from "lucide-react";
import { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { DeleteDialog } from "@/components/dialog/delete.dialog.tsx";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { Button } from "@/components/shadcn/button.tsx";
import { DataTable } from "@/components/table/data.table.tsx";
import { TableColumnHeader } from "@/components/table/table.column.header.tsx";
import { TableDataProvider, useTableData } from "@/components/table/table.data.context.tsx";
import { TableFooter } from "@/components/table/table.footer.tsx";
import { RowAction, TableRowActions } from "@/components/table/table.row.actions.tsx";
import { tableRowCheckbox } from "@/components/table/table.row.checkbox.tsx";
import { TableViewOptions } from "@/components/table/table.view.options.tsx";
import { DeleteButton } from "@/components/ui/delete.button.tsx";
import { StatusSwitch } from "@/components/ui/status.switch.tsx";
import { useDataTable } from "@/hooks/use.data.table.tsx";
import { RoleEditDialog } from "@/pages/role/role.edit.dialog.tsx";
import { valuesResolver } from "@/pages/role/role.filter.form.schema.tsx";
import { RoleFilterForm } from "@/pages/role/role.filter.form.tsx";
import { RoleActions } from "@/sys/role/role.actions.ts";
import { Role, RoleQuery } from "@/sys/role/role.types.ts";

const i18nKey = (id: string): string => `role.header.${id}`;

export const RolesComponent: FC = () => {
  const { t } = useTranslation();
  const { loadData, loading, pageable, records, setRecords } = useTableData<RoleQuery, Role>();
  const [action, setAction] = useState<RowAction<Role> | null>(null);

  const onDelete = async (ids: (string | undefined)[]): Promise<boolean> => {
    if (await RoleActions.delete(ids)) {
      await loadData();
      emitter.emit("error", ErrorCode.OK);
      setAction(null);
      table.resetRowSelection();
      return true;
    }
    return false;
  };

  const columns: ColumnDef<Role>[] = [
    tableRowCheckbox(),
    {
      accessorKey: "id",
      cell: ({ row }) => <div className="leading-6">{row.original.id}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "name",
      cell: ({ row }) => <div className="whitespace-nowrap">{row.original.name}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "code",
      cell: ({ row }) => <div className="whitespace-nowrap">{row.original.code}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "status",
      cell: ({ row }) => (
        <StatusSwitch
          i18nKey={`role.status.${RoleStatus[row.original.status]}`}
          checked={row.original.status === RoleStatus.ACTIVE}
          onCheckedChange={async (checked) => {
            const id = row.original.id;
            const status = checked ? RoleStatus.ACTIVE : RoleStatus.INACTIVE;
            if (await RoleActions.updateStatus(id, status)) {
              setRecords(records.map((x) => (x.id === id ? { ...x, status } : x)));
            }
          }}
        />
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
    {
      cell: ({ row }) => <TableRowActions item={row.original} setAction={setAction} />,
      enableHiding: false,
      id: "actions",
    },
  ];

  const table = useDataTable({ columns, data: records, pageable });
  const selected = table.getSelectedRowModel().rows.map((x) => x.original);

  return (
    <div className="p-4 md:px-8 space-y-2 md:space-y-4 h-full flex flex-col">
      <div className="flex items-center justify-between space-y-2 flex-wrap">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">{t("role.title.")}</h2>
          <p className="text-muted-foreground">{t("role.title.sub")}</p>
        </div>
      </div>

      <div className="flex items-center justify-end xl:justify-between space-x-4">
        <RoleFilterForm />

        <div className="flex items-center gap-4">
          <Button className="h-8" size="sm" onClick={() => setAction({ type: "create" })}>
            <PlusIcon />
            <span className="hidden lg:block">{t("action.add")}</span>
          </Button>

          <DeleteButton
            disabled={!selected.length}
            onDelete={() => onDelete(selected.map((x) => x.id))}
          />

          <TableViewOptions i18nKey={i18nKey} table={table} />
        </div>
      </div>

      <DataTable table={table} loading={loading} />

      <TableFooter />

      <DeleteDialog
        open={action?.type === "delete"}
        onCancel={() => setAction(null)}
        onContinue={() => onDelete([action?.item?.id])}
      />

      <RoleEditDialog
        role={action?.item}
        open={action?.type === "edit" || action?.type === "create"}
        onOpenChange={(open) => !open && setAction(null)}
      />
    </div>
  );
};

export const RolesPage: FC = () => {
  const params = new URLSearchParams(window.location.search);

  return (
    <LayoutContent fixed>
      <TableDataProvider
        loadData={RoleActions.list}
        pageable={params.size ? toPageable(params) : DEFAULT_PAGEABLE}
        query={params.size ? valuesResolver.resolve(params) : {}}
      >
        <RolesComponent />
      </TableDataProvider>
    </LayoutContent>
  );
};
