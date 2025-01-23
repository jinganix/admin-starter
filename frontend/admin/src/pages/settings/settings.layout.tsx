import { KeyRoundIcon, UserIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Outlet } from "react-router-dom";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { Separator } from "@/components/shadcn/separator.tsx";
import { SidebarNav } from "@/components/sidebar/sidebar.nav";

export const SettingsLayout: FC = () => {
  const { t } = useTranslation();

  return (
    <LayoutContent className="p-4 md:px-8">
      <div className="space-y-0.5">
        <h1 className="text-2xl font-bold tracking-tight md:text-3xl">{t("settings.title")}</h1>
        <p className="text-muted-foreground">{t("settings.description")}</p>
      </div>
      <Separator className="my-4 lg:my-6" />
      <div className="flex flex-1 flex-col space-y-2 md:space-y-2 overflow-hidden lg:flex-row lg:space-x-12 lg:space-y-0">
        <aside className="top-0 lg:sticky lg:w-1/5">
          <SidebarNav
            items={[
              {
                href: "/settings/profile",
                icon: <UserIcon size={18} />,
                title: t("settings.profile."),
              },
              {
                href: "/settings/credential",
                icon: <KeyRoundIcon size={18} />,
                title: t("settings.credential."),
              },
            ]}
          />
        </aside>
        <div className="flex w-full p-1 pr-4 overflow-y-hidden">
          <Outlet />
        </div>
      </div>
    </LayoutContent>
  );
};
