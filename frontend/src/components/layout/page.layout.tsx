import { FC } from "react";
import { Outlet } from "react-router-dom";
import { LanguageSwitch } from "@/components/layout/language.switch.tsx";
import { ThemeCustomizer } from "@/components/theme/theme.customizer.tsx";
import { GithubLink } from "@/components/ui/github.link.tsx";

export const PageLayout: FC = () => {
  return (
    <>
      <div className="absolute top-4 right-8 flex items-center space-x-4">
        <ThemeCustomizer />
        <LanguageSwitch />
        <GithubLink />
      </div>
      <Outlet />
    </>
  );
};
