import "@/helpers/i18n/i18n.config";
import { TokenService } from "@helpers/network/token.service.ts";
import { FC, useEffect } from "react";
import { container } from "tsyringe";
import { AppRouter } from "@/components/routes/app.router.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";
import { Toaster } from "@/components/ui/toaster.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { authStore } from "@/sys/auth/auth.store.ts";

export const App: FC = () => {
  const [loading, loadUser] = useLoading(
    () => authStore.initialize(async () => await container.resolve(TokenService).deleteToken()),
    true,
  );

  useEffect(() => void loadUser(), []);

  return (
    <>
      {!loading && <AppRouter />}
      {loading && <Spinner test-id="spinner" loading={loading} iconClass="size-10 md:size-12" />}
      <Toaster />
    </>
  );
};
