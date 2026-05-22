import { hasAuthority } from "@helpers/condition/cond.utils.ts";
import { emitter } from "@helpers/event/emitter.ts";
import { DEFAULT_PAGEABLE } from "@helpers/paging/pageable.ts";
import { toPageable } from "@helpers/search.params.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { UserStatus } from "@proto/SysUserProto.ts";
import { ColumnDef } from "@tanstack/react-table";
import dayjs from "dayjs";
import { PlusIcon } from "lucide-react";
import { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { CondComponent } from "@/components/condition/cond.component";
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
import { UserCreateDialog } from "@/pages/user/user.create.dialog.tsx";
import { valuesResolver } from "@/pages/user/user.filter.form.schema.tsx";
import { UserFilterForm } from "@/pages/user/user.filter.form.tsx";
import { UserUpdateDialog } from "@/pages/user/user.update.dialog.tsx";
import { Authority } from "@/sys/authority/authority.ts";
import { UserActions } from "@/sys/user/user.actions.ts";
import { User, UserQuery } from "@/sys/user/user.types.ts";

export type UserRowAction = RowAction<User>;
const i18nKey = (id: string): string => `user.header.${id}`;

export const UsersComponent: FC = () => {
  const { t } = useTranslation();
  const { loadData, loading, pageable, records, setRecords } = useTableData<UserQuery, User>();
  const [action, setAction] = useState<UserRowAction | null>(null);

  const onDelete = async (ids: (string | undefined)[]): Promise<boolean> => {
    if (await UserActions.delete(ids)) {
      await loadData();
      emitter.emit("error", ErrorCode.OK);
      setAction(null);
      table.resetRowSelection();
      return true;
    }
    return false;
  };

  const columns: ColumnDef<User>[] = [
    tableRowCheckbox(),
    {
      accessorKey: "id",
      cell: ({ row }) => <div className="leading-6">{row.original.id}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "username",
      cell: ({ row }) => <div className="whitespace-nowrap">{row.original.username}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "nickname",
      cell: ({ row }) => <div className="whitespace-nowrap">{row.original.nickname}</div>,
      header: ({ column }) => <TableColumnHeader column={column} i18nKey={i18nKey} />,
    },
    {
      accessorKey: "status",
      cell: ({ row }) => (
        <StatusSwitch
          i18nKey={`user.status.${UserStatus[row.original.status]}`}
          checked={row.original.status === UserStatus.ACTIVE}
          onCheckedChange={async (checked) => {
            const id = row.original.id;
            const status = checked ? UserStatus.ACTIVE : UserStatus.INACTIVE;
            if (await UserActions.updateStatus(id, status)) {
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
          <h2 className="text-2xl font-bold tracking-tight">{t("user.title.")}</h2>
          <p className="text-muted-foreground">{t("user.title.sub")}</p>
        </div>
      </div>

      <div className="flex items-center justify-end xl:justify-between space-x-4">
        <UserFilterForm />

        <div className="flex items-center gap-4">
          <CondComponent cond={hasAuthority(Authority.BTN_ADD_USER)}>
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

      <UserCreateDialog
        open={action?.type === "create"}
        onOpenChange={(open) => !open && setAction(null)}
      />

      <UserUpdateDialog
        userId={(action?.item as User)?.id}
        open={action?.type === "edit"}
        onOpenChange={(open) => !open && setAction(null)}
      />
    </div>
  );
};

export const UsersPage: FC = () => {
  const params = new URLSearchParams(window.location.search);

  return (
    <LayoutContent fixed>
      <TableDataProvider
        loadData={UserActions.list}
        pageable={params.size ? toPageable(params) : DEFAULT_PAGEABLE}
        query={params.size ? valuesResolver.resolve(params) : {}}
      >
        <UsersComponent />
      </TableDataProvider>
    </LayoutContent>
  );
};
