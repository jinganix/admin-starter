import { Option } from "@helpers/option.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { UserStatus } from "@proto/SysUserProto.ts";
import { ReactNode, useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { LabelAlignerProvider } from "@/components/form/aligned.label.tsx";
import { LabeledFormItem } from "@/components/form/labeled.form.item.tsx";
import { Button } from "@/components/shadcn/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/shadcn/dialog";
import { Form, FormField } from "@/components/shadcn/form";
import { Input } from "@/components/shadcn/input.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { FacetedFilter } from "@/components/ui/faceted.filter.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { RoleActions } from "@/sys/role/role.actions.ts";
import { Role, RoleQuery } from "@/sys/role/role.types.ts";
import { UserActions } from "@/sys/user/user.actions.ts";

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function UserCreateDialog({ open, onOpenChange }: Props): ReactNode {
  const { t } = useTranslation();
  const [options, setOptions] = useState<Option<string>[]>([]);
  const { loadData } = useTableData<RoleQuery, Role>();

  const formSchema = z.object({
    password: z.string().min(6, t("auth.password.min")).max(20, t("auth.password.max")),
    roles: z.array(z.string()).optional(),
    status: z.nativeEnum(UserStatus),
    username: z.string().min(3, t("auth.username.min")).max(20, t("auth.username.max")),
  });

  type FormValues = z.infer<typeof formSchema>;

  const values = {
    password: "",
    roles: undefined,
    status: UserStatus.ACTIVE,
    username: "",
  } satisfies FormValues;

  const form = useForm<FormValues>({
    defaultValues: values,
    resolver: zodResolver(formSchema),
  });

  useEffect(() => void (open && RoleActions.getOptions().then((x) => setOptions(x))), [open]);

  const changeOpen = (state: boolean): void => {
    form.reset();
    onOpenChange(state);
  };

  const [submitting, onSubmit] = useLoading(
    async ({ roles, ...values }: FormValues): Promise<void> => {
      const roleIds = roles ? roles : [];
      if (await UserActions.create({ ...values, roleIds })) {
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
          <DialogTitle>{t("user.dialog.create.title")}</DialogTitle>
        </DialogHeader>
        <LabelAlignerProvider>
          <Form {...form}>
            <form id="user-form" onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 p-0.5">
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <LabeledFormItem label={t("user.dialog.create.username")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <LabeledFormItem label={t("user.dialog.create.password")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="status"
                render={({ field }) => (
                  <LabeledFormItem label={t("user.dialog.create.status")}>
                    <Switch
                      checked={field.value === UserStatus.ACTIVE}
                      onCheckedChange={(x) =>
                        field.onChange(x ? UserStatus.ACTIVE : UserStatus.INACTIVE)
                      }
                    />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="roles"
                render={() => (
                  <LabeledFormItem label={t("user.dialog.create.roles")}>
                    <FacetedFilter
                      options={options}
                      setSelected={(values) => form.setValue("roles", values)}
                      selected={form.getValues("roles")}
                      maxShowed={Number.MAX_VALUE}
                    />
                  </LabeledFormItem>
                )}
              />
            </form>
          </Form>
        </LabelAlignerProvider>

        <DialogFooter>
          <Button disabled={submitting} type="submit" form="user-form">
            <Spinner loading={submitting} />
            {t("user.dialog.create.submit")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
