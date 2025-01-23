import { emitter } from "@helpers/event/emitter.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { ReactNode } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { Button } from "@/components/shadcn/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/shadcn/form";
import { Input } from "@/components/shadcn/input";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { SettingsContainer } from "@/pages/settings/settings.container.tsx";
import { authStore } from "@/sys/auth/auth.store.ts";
import { updateProfile } from "@/sys/user/user.actions.ts";

export function SettingsProfilePage(): ReactNode {
  const { t } = useTranslation();

  const formSchema = z.object({
    nickname: z
      .string()
      .min(3, t("settings.profile.nickname.min"))
      .max(20, t("settings.profile.nickname.max")),
  });
  type FormValues = z.infer<typeof formSchema>;

  const form = useForm<FormValues>({
    defaultValues: {
      nickname: authStore.nickname,
    },
    mode: "onChange",
    resolver: zodResolver(formSchema),
  });

  const [submitting, onSubmit] = useLoading(async (data: FormValues) => {
    if (await updateProfile(data.nickname)) {
      emitter.emit("error", ErrorCode.OK);
    }
  }, false);

  return (
    <SettingsContainer title={t("settings.profile.")} desc={t("settings.profile.description")}>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="nickname"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t("settings.profile.nickname.")}</FormLabel>
                <FormControl>
                  <Input
                    className="placeholder:text-sm"
                    placeholder={t("settings.profile.nickname.min")}
                    {...field}
                  />
                </FormControl>
                <FormDescription>{t("settings.profile.nickname.description")}</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button disabled={submitting} type="submit">
            <Spinner loading={submitting} />
            {t("settings.profile.submit")}
          </Button>
        </form>
      </Form>
    </SettingsContainer>
  );
}
