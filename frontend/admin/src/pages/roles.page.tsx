import { Paging } from "@helpers/paging/paging.ts";
import { defaultSearchParams, toPageable, toSearchParams } from "@helpers/search.params.ts";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import i18next from "i18next";
import { PlusIcon } from "lucide-react";
import { observer } from "mobx-react-lite";
import { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { DeleteDialog } from "@/components/dialog/delete.dialog.tsx";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
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
import { RoleEditDialog } from "@/pages/role/role.edit.dialog.tsx";
import { FormValues, useFilterForm } from "@/pages/role/role.filter.form.schema.tsx";
import { RoleFilterForm } from "@/pages/role/role.filter.form.tsx";
import { Role } from "@/sys/role/role.ts";
import { rolesStore } from "@/sys/role/roles.store";

const i18nKey = (id: string): string => `role.header.${id}`;

export const columns = (setAction: (action: RowAction<Role>) => void): ColumnDef<Role>[] => [
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
      <div className="flex justify-center items-center space-x-2">
        <Switch
          id="status"
          checked={row.original.status === RoleStatus.ACTIVE}
          onCheckedChange={async (checked) => {
            await rolesStore.toggleStatus(
              row.original.id,
              checked ? RoleStatus.ACTIVE : RoleStatus.INACTIVE,
            );
          }}
        />
        <Label htmlFor="status">
          {i18next.t(`role.status.${RoleStatus[row.original.status]}`)}
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

export const RolesComponent: FC = observer(() => {
  const { t } = useTranslation();
  const [params, setParams] = useTableParams();
  const [action, setAction] = useState<RowAction<Role> | null>(null);

  const form = useFilterForm(params);

  const pageable = toPageable(params);
  const [loading, loadData] = useLoading(async (data?: FormValues): Promise<void> => {
    data = data || form.getValues();
    setParams(toSearchParams({ ...pageable, ...data }));
    params.size && (await rolesStore.load(pageable, data.name, data.status));
  }, false);
  const onDelete = async (ids: (string | undefined)[]): Promise<boolean> => {
    const filtered = ids.filter((x) => x !== undefined);
    if (!filtered.length) {
      return true;
    }
    if (await rolesStore.delete(filtered)) {
      setAction(null);
      table.resetRowSelection();
      return true;
    }
    return false;
  };

  useEffect(() => void loadData(), [params]);
  const { paging, records } = rolesStore;
  const table = useDataTable({ columns: columns(setAction), data: records, pageable });
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
        <RoleFilterForm form={form} loadData={loadData} />

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

      <TableFooter paging={paging || new Paging(pageable.size)} />

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
});

export const RolesPage: FC = () => {
  return (
    <LayoutContent fixed>
      <TableParamsProvider value={defaultSearchParams()}>
        <RolesComponent />
      </TableParamsProvider>
    </LayoutContent>
  );
};
