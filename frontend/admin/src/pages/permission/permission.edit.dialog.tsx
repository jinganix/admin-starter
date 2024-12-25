import { enumToOptions } from "@helpers/option.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { ReactNode, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { LabelAlignerProvider } from "@/components/form/aligned.label";
import { LabeledFormItem } from "@/components/form/labeled.form.item.tsx";
import { Button } from "@/components/shadcn/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/shadcn/dialog";
import { Form, FormControl, FormField } from "@/components/shadcn/form";
import { Input } from "@/components/shadcn/input.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/shadcn/select.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { Permission } from "@/sys/permission/permission.ts";
import { permissionsStore } from "@/sys/permission/permissions.store.ts";

const formSchema = z.object({
  code: z.string().min(1, { message: "Permission code is required." }),
  description: z.string().optional(),
  name: z.string().min(1, { message: "Name is required." }),
  status: z.nativeEnum(PermissionStatus),
  type: z.nativeEnum(PermissionType),
});

interface Props {
  permission?: Permission;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function PermissionEditDialog({ permission, open, onOpenChange }: Props): ReactNode {
  const { t } = useTranslation();

  const values = {
    code: permission?.code || "",
    description: permission?.description || "",
    name: permission?.name || "",
    status: permission?.status ?? PermissionStatus.ACTIVE,
    type: permission?.type ?? PermissionType.API,
  };

  const form = useForm<z.infer<typeof formSchema>>({
    defaultValues: values,
    resolver: zodResolver(formSchema),
  });

  useEffect(() => permission && form.reset(values), [permission]);

  const changeOpen = (state: boolean): void => {
    form.reset();
    onOpenChange(state);
  };

  const [submitting, onSubmit] = useLoading(
    async (values: z.infer<typeof formSchema>): Promise<void> => {
      if (permission) {
        if (await permissionsStore.update(permission.id, values)) {
          changeOpen(false);
        }
      } else if (await permissionsStore.create(values)) {
        changeOpen(false);
      }
    },
    false,
  );

  return (
    <Dialog open={open} onOpenChange={(state) => changeOpen(state)}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader className="text-left">
          <DialogTitle>
            {permission ? t("permission.dialog.update") : t("permission.dialog.create")}
          </DialogTitle>
        </DialogHeader>
        <LabelAlignerProvider>
          <Form {...form}>
            <form id="user-form" onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 p-0.5">
              <FormField
                control={form.control}
                name="type"
                render={({ field }) => (
                  <LabeledFormItem label={t("permission.dialog.type")} controlled={false}>
                    <Select
                      onValueChange={(x) => field.onChange(Number(x))}
                      defaultValue={String(field.value)}
                    >
                      <FormControl>
                        <SelectTrigger className="col-span-2">
                          <SelectValue placeholder="Select a verified email to display" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {enumToOptions(PermissionType, `permission.type`).map((x) => (
                          <SelectItem key={x.value} value={`${x.value}`}>
                            {x.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <LabeledFormItem label={t("permission.dialog.name")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="code"
                render={({ field }) => (
                  <LabeledFormItem label={t("permission.dialog.code")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <LabeledFormItem label={t("permission.dialog.description")}>
                    <Input {...field} autoComplete="off" />
                  </LabeledFormItem>
                )}
              />

              <FormField
                control={form.control}
                name="status"
                render={({ field }) => (
                  <LabeledFormItem label={t("permission.dialog.status")}>
                    <Switch
                      checked={field.value === PermissionStatus.ACTIVE}
                      onCheckedChange={(x) =>
                        field.onChange(x ? PermissionStatus.ACTIVE : PermissionStatus.INACTIVE)
                      }
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
            {t("permission.dialog.submit")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
