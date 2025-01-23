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
import { Skeleton } from "@/components/shadcn/skeleton.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { FacetedFilter } from "@/components/utils/faceted.filter.tsx";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { getRoleOptions } from "@/sys/role/role.actions.ts";
import { retrieveUser } from "@/sys/user/user.actions.ts";
import { User } from "@/sys/user/user.ts";
import { usersStore } from "@/sys/user/users.store.ts";

interface Props {
  userId: string;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function UserUpdateDialog({ userId, open, onOpenChange }: Props): ReactNode {
  const { t } = useTranslation();
  const [user, setUser] = useState<User | null>(null);
  const [options, setOptions] = useState<Option<string>[]>([]);

  const formSchema = z.object({
    nickname: z
      .string()
      .min(3, t("settings.profile.nickname.min"))
      .max(20, t("settings.profile.nickname.max")),
    roles: z.array(z.string()).optional(),
    status: z.nativeEnum(UserStatus),
  });

  type FormValues = z.infer<typeof formSchema>;

  const values = {
    nickname: user?.nickname || "",
    roles: user?.roleIds,
    status: user?.status ?? UserStatus.ACTIVE,
  } satisfies FormValues;

  const form = useForm<FormValues>({
    defaultValues: values,
    resolver: zodResolver(formSchema),
  });

  const [loading, loadData] = useLoading(async () => {
    const [user, options] = await Promise.all([retrieveUser(userId), getRoleOptions()]);
    setUser(user);
    setOptions(options);
  }, true);

  useEffect(() => void (user && form.reset(values)), [user]);
  useEffect(() => void (open && loadData()), [open]);

  const changeOpen = (state: boolean): void => {
    form.reset();
    onOpenChange(state);
  };

  const [submitting, onSubmit] = useLoading(
    async ({ roles, ...values }: FormValues): Promise<void> => {
      const roleIds = roles ? roles : [];
      if (user) {
        if (await usersStore.update({ ...values, id: user.id, roleIds })) {
          changeOpen(false);
        }
      }
    },
    false,
  );

  return (
    <Dialog open={open} onOpenChange={(state) => changeOpen(state)}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader className="text-left">
          <DialogTitle>{t("user.dialog.update.title")}</DialogTitle>
        </DialogHeader>

        {loading && (
          <div className="space-y-4">
            <Skeleton className="h-8 w-full" />
            <Skeleton className="h-8 w-full" />
            <Skeleton className="h-8 w-full" />
          </div>
        )}

        {!loading && (
          <LabelAlignerProvider>
            <Form {...form}>
              <form
                id="user-form"
                onSubmit={form.handleSubmit(onSubmit)}
                className="space-y-4 p-0.5"
              >
                <FormField
                  control={form.control}
                  name="nickname"
                  render={({ field }) => (
                    <LabeledFormItem label={t("user.dialog.update.nickname")}>
                      <Input {...field} autoComplete="off" />
                    </LabeledFormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="status"
                  render={({ field }) => (
                    <LabeledFormItem label={t("user.dialog.update.status")}>
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
                    <LabeledFormItem label={t("user.dialog.update.roles")}>
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
        )}

        <DialogFooter>
          <Button disabled={submitting} type="submit" form="user-form">
            <Spinner loading={submitting} />
            {t("user.dialog.update.submit")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
