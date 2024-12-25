import { zodResolver } from "@hookform/resolvers/zod";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { find, startsWith } from "lodash";
import { ReactNode, useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import {
  AlignedLabel,
  LabelAligner,
  LabelAlignerProvider,
} from "@/components/form/aligned.label.tsx";
import { LabeledFormItem } from "@/components/form/labeled.form.item.tsx";
import { Button } from "@/components/shadcn/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/shadcn/dialog";
import { Form, FormField, FormItem } from "@/components/shadcn/form";
import { Input } from "@/components/shadcn/input.tsx";
import { Separator } from "@/components/shadcn/separator.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { Textarea } from "@/components/shadcn/textarea.tsx";
import { TreeItem } from "@/components/tree/tree.view.item.tsx";
import { TreeView } from "@/components/tree/tree.view.tsx";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { getPermissionOptions } from "@/sys/permission/permission.actions.ts";
import { PermissionOption } from "@/sys/permission/permission.types.ts";
import { Role } from "@/sys/role/role.ts";
import { rolesStore } from "@/sys/role/roles.store.ts";

const formSchema = z.object({
  code: z.string().min(1, { message: "Role code is required." }),
  description: z.string().optional(),
  name: z.string().min(1, { message: "Name is required." }),
  permissions: z.array(z.string()).optional(),
  status: z.nativeEnum(RoleStatus),
});

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
  labelResizer?: LabelAligner;
}

export function RoleEditDialog({ role, open, onOpenChange }: Props): ReactNode {
  const { t } = useTranslation();
  const [treeItems, setTreeItems] = useState<TreeItem<string>[]>([]);

  const values = {
    code: role?.code || "",
    description: role?.description ? t(role.description) : "",
    name: role?.name || "",
    permissions: role?.permissionIds,
    status: role?.status ?? RoleStatus.ACTIVE,
  };

  const form = useForm<z.infer<typeof formSchema>>({
    defaultValues: values,
    resolver: zodResolver(formSchema),
  });

  useEffect(() => form.reset(values), [role]);
  useEffect(
    () =>
      void (
        open &&
        getPermissionOptions().then((options) =>
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
    async ({ permissions, ...values }: z.infer<typeof formSchema>): Promise<void> => {
      const permissionIds = permissions ? permissions : [];
      if (role) {
        if (await rolesStore.update(role.id, { ...values, permissionIds })) {
          changeOpen(false);
        }
      } else if (await rolesStore.create({ ...values, permissionIds })) {
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
        <LabelAlignerProvider>
          <Form {...form}>
            <form id="user-form" onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 p-0.5">
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
                  <FormItem>
                    <AlignedLabel>{t("role.dialog.permissions")}</AlignedLabel>
                    <TreeView
                      items={treeItems}
                      title={t("role.dialog.filter")}
                      className="rounded-md w-full"
                      setSelected={(values) => form.setValue("permissions", values)}
                      selected={form.getValues("permissions")}
                    />
                  </FormItem>
                )}
              />
            </form>
          </Form>
        </LabelAlignerProvider>

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
