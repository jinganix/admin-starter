import { ReactNode } from "react";
import { BrowserRouter } from "react-router";
import { AppRoutes } from "@/components/routes/app.routes.tsx";

export function AppRouter(): ReactNode {
  return (
    <BrowserRouter test-id="browser-router">
      <AppRoutes />
    </BrowserRouter>
  );
}
