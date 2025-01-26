import "@/helpers/i18n/i18n.config";
import { TokenService } from "@helpers/network/token.service.ts";
import { FC, useEffect } from "react";
import { BrowserRouter, Routes } from "react-router";
import { container } from "tsyringe";
import { Spinner } from "@/components/ui/spinner.tsx";
import { Toaster } from "@/components/ui/toaster.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { getRoutes } from "@/routes.tsx";
import { authStore } from "@/sys/auth/auth.store.ts";

export const App: FC = () => {
  const [loading, loadUser] = useLoading(
    () => authStore.initialize(async () => await container.resolve(TokenService).deleteToken()),
    true,
  );

  useEffect(() => void loadUser(), []);

  return (
    <>
      {!loading && (
        <BrowserRouter test-id="browser-router">
          <Routes>{...getRoutes()}</Routes>
        </BrowserRouter>
      )}
      {loading && <Spinner test-id="spinner" loading={loading} size={40} />}
      <Toaster />
    </>
  );
};
