import { zodResolver } from "@hookform/resolvers/zod";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { find, startsWith } from "lodash";
import { ReactNode, useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { LabeledFormItem } from "@/components/form/labeled.form.item.tsx";
import { Button } from "@/components/shadcn/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/shadcn/dialog";
import { Form, FormField, FormItem, FormLabel } from "@/components/shadcn/form";
import { Input } from "@/components/shadcn/input.tsx";
import { ScrollArea } from "@/components/shadcn/scroll-area";
import { Separator } from "@/components/shadcn/separator.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { Textarea } from "@/components/shadcn/textarea.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { TreeItem } from "@/components/tree/tree.view.item.tsx";
import { TreeView } from "@/components/tree/tree.view.tsx";
import { TreeStateProvider } from "@/components/tree/use.tree.state.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { PermissionActions } from "@/sys/permission/permission.actions.ts";
import { PermissionOption } from "@/sys/permission/permission.types.ts";
import { RoleActions } from "@/sys/role/role.actions.ts";
import { Role, RoleQuery } from "@/sys/role/role.types.ts";

function putTreeItems(
  option: PermissionOption,
  items: TreeItem<string>[] = [],
): TreeItem<string>[] {
  const item = find(items, (x) => startsWith(option.code, x.code));
  if (item) {
    item.items = putTreeItems(option, item.items);
  } else {
    items.push(option);
  }
  return items;
}

function toTreeItems(options: PermissionOption[]): TreeItem<string>[] {
  options = options.sort((a, b) => a.code.length - b.code.length);
  const items: TreeItem<string>[] = [];
  options.forEach((x) => putTreeItems(x, items));
  return items;
}

interface Props {
  role?: Role;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function RoleEditDialog({ role, open, onOpenChange }: Props): ReactNode {
  const { t } = useTranslation();
  const { records, setRecords, loadData } = useTableData<RoleQuery, Role>();
  const [treeItems, setTreeItems] = useState<TreeItem<string>[]>([]);

  const values = {
    code: role?.code || "",
    description: role?.description ? t(role.description) : "",
    name: role?.name || "",
    permissions: role?.permissionIds,
    status: role?.status ?? RoleStatus.ACTIVE,
  };

  const formSchema = z.object({
    code: z.string().min(3, t("role.edit.code.min")).max(20, t("role.edit.code.max")),
    description: z.string().optional(),
    name: z.string().min(3, t("role.edit.name.min")).max(40, t("role.edit.name.max")),
    permissions: z.array(z.string()).optional(),
    status: z.nativeEnum(RoleStatus),
  });
  type FormValues = z.infer<typeof formSchema>;

  const form = useForm<FormValues>({
    defaultValues: values,
    resolver: zodResolver(formSchema),
  });

  useEffect(() => form.reset(values), [role]);
  useEffect(
    () =>
      void (
        open &&
        PermissionActions.getOptions().then((options) =>
          setTreeItems(toTreeItems(options.map((x) => ({ ...x, label: t(x.label) })))),
        )
      ),
    [t, open],
  );

  const changeOpen = (state: boolean): void => {
    form.reset();
    onOpenChange(state);
  };

  const [submitting, onSubmit] = useLoading(
    async ({ permissions, ...values }: FormValues): Promise<void> => {
      const permissionIds = permissions ? permissions : [];
      if (role) {
        const newItem = await RoleActions.update(role.id, { ...values, permissionIds });
        if (newItem) {
          setRecords(records.map((x) => (x.id === newItem.id ? newItem : x)));
          changeOpen(false);
        }
      } else if (await RoleActions.create({ ...values, permissionIds })) {
        await loadData();
        changeOpen(false);
      }
    },
    false,
  );

  return (
    <Dialog open={open} onOpenChange={(state) => changeOpen(state)}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader className="text-left">
          <DialogTitle>{role ? t("role.dialog.update") : t("role.dialog.create")}</DialogTitle>
        </DialogHeader>
        <ScrollArea className="h-[60vh] w-full py-2 overflow-x-scroll">
          <Form {...form}>
            <form
              id="user-form"
              onSubmit={form.handleSubmit(onSubmit)}
              className="border-spacing-y-4 p-1"
            >
              <FormField
                control={form.control}
                name="code"
                render={({ field }) => (
                  <LabeledFormItem label={t("role.dialog.code")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <LabeledFormItem label={t("role.dialog.description")}>
                    <Textarea {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <LabeledFormItem label={t("role.dialog.name")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="status"
                render={({ field }) => (
                  <LabeledFormItem label={t("role.dialog.status")}>
                    <Switch
                      checked={field.value === RoleStatus.ACTIVE}
                      onCheckedChange={(x) =>
                        field.onChange(x ? RoleStatus.ACTIVE : RoleStatus.INACTIVE)
                      }
                    />
                  </LabeledFormItem>
                )}
              />

              <Separator />

              <FormField
                control={form.control}
                name="permissions"
                render={() => (
                  <>
                    <div className="table-row space-x-4">
                      <div className="table-cell w-[1%] min-w-16 whitespace-nowrap text-right">
                        <FormLabel className="text-right">{t("role.dialog.permissions")}</FormLabel>
                      </div>
                      <div />
                    </div>
                    <FormItem className="max-w-full space-y-0">
                      <TreeStateProvider>
                        <TreeView
                          items={treeItems}
                          title={t("role.dialog.filter")}
                          className="w-full"
                          setSelected={(values) => form.setValue("permissions", values)}
                          selected={form.getValues("permissions")}
                        />
                      </TreeStateProvider>
                    </FormItem>
                  </>
                )}
              />
            </form>
          </Form>
        </ScrollArea>

        <DialogFooter>
          <Button disabled={submitting} type="submit" form="user-form">
            <Spinner loading={submitting} />
            {t("role.dialog.submit")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
