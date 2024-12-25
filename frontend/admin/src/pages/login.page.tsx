import { zodResolver } from "@hookform/resolvers/zod";
import { FC } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { Link } from "react-router";
import { container } from "tsyringe";
import { z } from "zod";
import { Button } from "@/components/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/shadcn/card";
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
import { TokenService } from "@/helpers/network/token.service.ts";
import { useLoading } from "@/hooks/use.loading.ts";
import { authStore } from "@/sys/auth/auth.store.ts";

export const LoginPage: FC = () => {
  const { t } = useTranslation();

  const formSchema = z.object({
    password: z.string().min(6, t("auth.password.min")).max(20, t("auth.password.max")),
    username: z.string().min(3, t("auth.username.min")).max(20, t("auth.username.max")),
  });
  type FormValues = z.infer<typeof formSchema>;

  const form = useForm<FormValues>({
    defaultValues: {
      password: "",
      username: "",
    },
    resolver: zodResolver(formSchema),
  });

  const [submitting, onSubmit] = useLoading(async (data: FormValues): Promise<void> => {
    if (await container.resolve(TokenService).auth(data.username, data.password)) {
      authStore.dispose();
      await authStore.initialize();
    }
  }, false);

  return (
    <div className="flex h-screen w-full items-center justify-center px-4">
      <Card className="mx-auto w-11/12 sm:w-8/12 md:w-6/12 lg:w-4/12 xl:w-3/12 max-w-[400px]">
        <CardHeader>
          <CardTitle className="text-2xl w-full text-center">{t("auth.title")}</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="grid gap-4">
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>{t("auth.username.")}</FormLabel>
                    <FormControl>
                      <Input placeholder={t("auth.username.")} {...field} />
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
                    <FormLabel>{t("auth.password.")}</FormLabel>
                    <FormControl>
                      <Input type="password" placeholder={t("auth.password.")} {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button disabled={submitting} type="submit" className="w-full mt-4">
                <Spinner loading={submitting} />
                {t("auth.login")}
              </Button>
            </form>
          </Form>

          <p className="mt-4 px-8 text-center text-sm text-muted-foreground">
            {t("auth.noAccount")}
            <Link to="/signup" className="ml-1 underline underline-offset-4 hover:text-primary">
              {t("auth.signup")}
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
};
