import { hasAuthority } from "@helpers/condition/cond.utils.ts";
import { emitter } from "@helpers/event/emitter.ts";
import { Trans } from "@helpers/i18n";
import { Paging } from "@helpers/paging/paging.ts";
import { defaultSearchParams, toPageable, toSearchParams } from "@helpers/search.params.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import i18next from "i18next";
import { PlusIcon } from "lucide-react";
import { observer } from "mobx-react-lite";
import { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useFilterForm } from "./permission/permission.filter.form.schema";
import { CondComponent } from "@/components/condition/cond.component.tsx";
import { DeleteDialog } from "@/components/dialog/delete.dialog.tsx";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { TableTitle } from "@/components/layout/table.title.tsx";
import { Button } from "@/components/shadcn/button.tsx";
import { Label } from "@/components/shadcn/label.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { DataTable } from "@/components/table/data.table.tsx";
import { RowAction, RowActions } from "@/components/table/row.actions.tsx";
import { TableColumnHeader } from "@/components/table/table.column.header.tsx";
import { TableFooter } from "@/components/table/table.footer.tsx";
import { TableParamsProvider, useTableParams } from "@/components/table/table.params.context.tsx";
import { tableRowCheckbox } from "@/components/table/table.row.checkbox.tsx";
import { TableViewOptions } from "@/components/table/table.view.options.tsx";
import { DeleteButton } from "@/components/utils/delete.button.tsx";
import { useDataTable } from "@/hooks/use.data.table.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { PermissionEditDialog } from "@/pages/permission/permission.edit.dialog.tsx";
import { PermissionFilterForm } from "@/pages/permission/permission.filter.form.tsx";
import { PermissionTableActions } from "@/pages/permission/permission.table.actions.tsx";
import { Authority } from "@/sys/authority/authority.ts";
import { Permission } from "@/sys/permission/permission.ts";
import { permissionsStore } from "@/sys/permission/permissions.store";

const i18nKey = (id: string): string => `permission.header.${id}`;

export const columns = (
  setAction: (action: RowAction<Permission>) => void,
  t: Trans,
): ColumnDef<Permission>[] => [
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
            await permissionsStore.toggleStatus(
              row.original.id,
              checked ? PermissionStatus.ACTIVE : PermissionStatus.INACTIVE,
            );
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
    cell: ({ row }) => <RowActions item={row.original} setAction={setAction} />,
    enableHiding: false,
    id: "actions",
  },
];

export const PermissionsComponent: FC = observer(() => {
  const { t } = useTranslation();
  const [params, setParams] = useTableParams();
  const [action, setAction] = useState<RowAction<Permission> | null>(null);

  const form = useFilterForm(params);

  const pageable = toPageable(params);
  const [loading, loadData] = useLoading(async (): Promise<void> => {
    const data = form.getValues();
    setParams(toSearchParams({ ...pageable, ...data }));
    params.size && (await permissionsStore.load(pageable, data.code, data.status, data.types));
  }, false);

  const onDelete = async (ids: (string | undefined)[]): Promise<boolean> => {
    const filtered = ids.filter((x) => x !== undefined);
    if (!filtered.length) {
      return true;
    }
    if (await permissionsStore.delete(filtered)) {
      emitter.emit("error", ErrorCode.OK);
      setAction(null);
      table.resetRowSelection();
      return true;
    }
    return false;
  };

  useEffect(() => void loadData(), [params]);
  const { paging, records } = permissionsStore;
  const table = useDataTable({ columns: columns(setAction, t), data: records, pageable });
  const selected = table.getSelectedRowModel().rows.map((x) => x.original);

  return (
    <div className="p-4 md:px-8 space-y-2 md:space-y-4 h-full flex flex-col">
      <div className="flex items-center justify-between space-y-2 flex-wrap">
        <TableTitle title={t("permission.title.")} sub={t("permission.title.sub")} />

        <PermissionTableActions />
      </div>

      <div className="flex items-center justify-end xl:justify-between space-x-4">
        <PermissionFilterForm form={form} loadData={loadData} />

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

      <TableFooter paging={paging || new Paging(pageable.size)} />

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
});

export const PermissionsPage: FC = () => {
  return (
    <LayoutContent fixed>
      <TableParamsProvider value={defaultSearchParams()}>
        <PermissionsComponent />
      </TableParamsProvider>
    </LayoutContent>
  );
};
