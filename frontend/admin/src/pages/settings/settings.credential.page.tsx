import { emitter } from "@helpers/event/emitter.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { FC } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { Button } from "@/components/shadcn/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/shadcn/form";
import { Input } from "@/components/shadcn/input";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { SettingsContainer } from "@/pages/settings/settings.container.tsx";
import { changePassword } from "@/sys/user/user.actions.ts";

export const SettingsCredentialPage: FC = () => {
  const { t } = useTranslation();

  const formSchema = z
    .object({
      confirmPassword: z.string(),
      current: z.string().min(6, t("auth.password.min")).max(20, t("auth.password.max")),
      password: z.string().min(6, t("auth.password.min")).max(20, t("auth.password.max")),
    })
    .refine((data) => data.password === data.confirmPassword, {
      message: t("auth.password.notMatch"),
      path: ["confirmPassword"],
    });
  type FormValues = z.infer<typeof formSchema>;

  const form = useForm<FormValues>({
    defaultValues: {
      confirmPassword: "",
      current: "",
      password: "",
    },
    resolver: zodResolver(formSchema),
  });

  const [submitting, onSubmit] = useLoading(async (data: FormValues): Promise<void> => {
    if (await changePassword(data.current, data.password)) {
      emitter.emit("error", ErrorCode.OK);
    }
  }, false);

  return (
    <SettingsContainer
      title={t("settings.credential.")}
      desc={t("settings.credential.description")}
    >
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="current"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t("settings.credential.current.")}</FormLabel>
                <FormControl>
                  <Input
                    type="password"
                    placeholder={t("settings.credential.current.placeholder")}
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t("settings.credential.password")}</FormLabel>
                <FormControl>
                  <Input
                    type="password"
                    placeholder={t("settings.credential.password")}
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="confirmPassword"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t("settings.credential.confirm")}</FormLabel>
                <FormControl>
                  <Input
                    type="password"
                    placeholder={t("settings.credential.confirm")}
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button disabled={submitting} type="submit">
            <Spinner loading={submitting} />
            {t("action.submit")}
          </Button>
        </form>
      </Form>
    </SettingsContainer>
  );
};
