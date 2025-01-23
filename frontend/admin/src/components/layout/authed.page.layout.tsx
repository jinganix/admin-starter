import { cn } from "@helpers/lib/cn.ts";
import { FC } from "react";
import { Outlet } from "react-router-dom";
import { Header } from "@/components/layout/header.tsx";
import { LanguageSwitch } from "@/components/layout/language.switch.tsx";
import { UserNav } from "@/components/layout/user.nav.tsx";
import { SidebarProvider } from "@/components/shadcn/sidebar";
import { AppSidebar } from "@/components/sidebar/app.sidebar";
import { ThemeCustomizer } from "@/components/theme/theme.customizer.tsx";

export const AuthedPageLayout: FC = () => {
  return (
    <SidebarProvider>
      <AppSidebar className="z-20" />
      <div
        id="content"
        className={cn(
          "max-w-full w-full ml-auto",
          "peer-data-[state=collapsed]:w-[calc(100%-var(--sidebar-width-icon))]",
          "peer-data-[state=expanded]:w-[calc(100%-var(--sidebar-width))]",
          "transition-[width] ease-linear duration-200",
          "h-svh flex flex-col",
          "group-data-[scroll-locked=1]/body:h-full",
          "group-data-[scroll-locked=1]/body:has-[main.fixed-main]:h-svh",
        )}
      >
        <Header fixed>
          <div className="ml-auto flex items-center space-x-4">
            <ThemeCustomizer />
            <LanguageSwitch />
            <UserNav />
          </div>
        </Header>
        <Outlet />
      </div>
    </SidebarProvider>
  );
};
