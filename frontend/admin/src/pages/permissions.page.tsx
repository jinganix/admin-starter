import { hasAuthority } from "@helpers/condition/cond.utils.ts";
import { emitter } from "@helpers/event/emitter.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { toPageable } from "@helpers/search.params.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import i18next from "i18next";
import { PlusIcon } from "lucide-react";
import { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { CondComponent } from "@/components/condition/cond.component.tsx";
import { DeleteDialog } from "@/components/dialog/delete.dialog.tsx";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { TableTitle } from "@/components/layout/table.title.tsx";
import { Button } from "@/components/shadcn/button.tsx";
import { Label } from "@/components/shadcn/label.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { DataTable } from "@/components/table/data.table.tsx";
import { TableColumnHeader } from "@/components/table/table.column.header.tsx";
import { TableDataProvider, useTableData } from "@/components/table/table.data.context.tsx";
import { TableFooter } from "@/components/table/table.footer.tsx";
import { RowAction, TableRowActions } from "@/components/table/table.row.actions.tsx";
import { tableRowCheckbox } from "@/components/table/table.row.checkbox.tsx";
import { TableViewOptions } from "@/components/table/table.view.options.tsx";
import { DeleteButton } from "@/components/utils/delete.button.tsx";
import { useDataTable } from "@/hooks/use.data.table.tsx";
import { PermissionEditDialog } from "@/pages/permission/permission.edit.dialog.tsx";
import { valuesResolver } from "@/pages/permission/permission.filter.form.schema.tsx";
import { PermissionFilterForm } from "@/pages/permission/permission.filter.form.tsx";
import { PermissionTableActions } from "@/pages/permission/permission.table.actions.tsx";
import { Authority } from "@/sys/authority/authority.ts";
import { PermissionActions } from "@/sys/permission/permission.actions.ts";
import { Permission, PermissionQuery } from "@/sys/permission/permission.types";

const i18nKey = (id: string): string => `permission.header.${id}`;

export const PermissionsComponent: FC = () => {
  const { t } = useTranslation();
  const { loadData, loading, pageable, records, setRecords } = useTableData<
    PermissionQuery,
    Permission
  >();
  const [action, setAction] = useState<RowAction<Permission> | null>(null);

  const onDelete = async (ids: (string | undefined)[]): Promise<boolean> => {
    if (await PermissionActions.delete(ids)) {
      await loadData();
      emitter.emit("error", ErrorCode.OK);
      setAction(null);
      table.resetRowSelection();
      return true;
    }
    return false;
  };

  const columns: ColumnDef<Permission>[] = [
    tableRowCheckbox(),
    {
      accessorKey: "id",
      cell: ({ row }) => <div className="leading-6">{row.original.id}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "name",
      cell: ({ row }) => <div className="whitespace-nowrap">{t(row.original.name)}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "code",
      cell: ({ row }) => <div>{row.original.code}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "type",
      cell: ({ row }) => <div>{t(`permission.type.${PermissionType[row.original.type]}`)}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "status",
      cell: ({ row }) => (
        <div className="flex justify-center items-center space-x-2">
          <Switch
            id="status"
            checked={row.original.status === PermissionStatus.ACTIVE}
            onCheckedChange={async (checked) => {
              const id = row.original.id;
              const status = checked ? PermissionStatus.ACTIVE : PermissionStatus.INACTIVE;
              if (await PermissionActions.updateStatus(id, status)) {
                setRecords(records.map((x) => (x.id === id ? { ...x, status } : x)));
              }
            }}
          />
          <Label htmlFor="status">
            {i18next.t(`permission.status.${PermissionStatus[row.original.status]}`)}
          </Label>
        </div>
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
        <TableTitle title={t("permission.title.")} sub={t("permission.title.sub")} />

        <PermissionTableActions />
      </div>

      <div className="flex items-center justify-end xl:justify-between space-x-4">
        <PermissionFilterForm />

        <div className="flex items-center gap-4">
          <CondComponent cond={hasAuthority(Authority.BTN_ADD_PERMISSION)}>
            <Button className="h-8" size="sm" onClick={() => setAction({ type: "create" })}>
              <PlusIcon />
              <span className="hidden lg:block">{t("action.add")}</span>
            </Button>
          </CondComponent>

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

      <PermissionEditDialog
        permission={action?.item}
        open={action?.type === "edit" || action?.type === "create"}
        onOpenChange={(open) => !open && setAction(null)}
      />
    </div>
  );
};

export const PermissionsPage: FC = () => {
  const params = new URLSearchParams(window.location.search);

  return (
    <LayoutContent fixed>
      <TableDataProvider
        loadData={PermissionActions.list}
        pageable={params.size ? toPageable(params) : DEFAULT_PAGEABLE}
        query={params.size ? valuesResolver.resolve(params) : {}}
      >
        <PermissionsComponent />
      </TableDataProvider>
    </LayoutContent>
  );
};
