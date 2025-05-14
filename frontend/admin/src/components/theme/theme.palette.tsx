import { cn } from "@helpers/lib/cn.ts";
import { CheckIcon, MoonIcon, RepeatIcon, SunIcon } from "lucide-react";
import { CSSProperties, FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button";
import { Label } from "@/components/shadcn/label";
import { Skeleton } from "@/components/shadcn/skeleton";
import { themeDefs } from "@/components/theme/theme.defs.ts";
import { useConfig } from "@/hooks/use.config.tsx";

export const ThemePalette: FC = () => {
  const { t } = useTranslation();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  const { config, setConfig, resetConfig } = useConfig();

  return (
    <div className="flex flex-col space-y-4 md:space-y-6 p-4 w-screen md:w-auto">
      <div className="flex items-start pt-4 md:pt-0">
        <div className="space-y-1 pr-2">
          <div className="font-semibold leading-none tracking-tight">{t("theme.title.")}</div>
          <div className="text-xs text-muted-foreground">{t("theme.title.sub")}</div>
        </div>
        <Button
          variant="ghost"
          size="icon"
          className="ml-auto rounded-[0.5rem]"
          onClick={() => resetConfig}
        >
          <RepeatIcon />
        </Button>
      </div>
      <div className="flex flex-1 flex-col space-y-4 md:space-y-6">
        <div className="space-y-1.5">
          <Label className="text-xs">{t("theme.color.")}</Label>
          <div className="grid grid-cols-3 gap-2">
            {themeDefs.map((theme) => {
              const isActive = config.theme === theme.name;

              return mounted ? (
                <Button
                  variant={"outline"}
                  size="sm"
                  key={theme.name}
                  onClick={() => setConfig({ ...config, theme: theme.name })}
                  className={cn("justify-start", isActive && "border-2 border-primary")}
                  style={
                    {
                      "--theme-primary": `hsl(${
                        theme?.activeColor[config.mode === "dark" ? "dark" : "light"]
                      })`,
                    } as CSSProperties
                  }
                >
                  <span
                    className={cn(
                      "mr-1 flex h-5 w-5 shrink-0 -translate-x-1 items-center justify-center rounded-full bg-(--theme-primary)",
                    )}
                  >
                    {isActive && <CheckIcon className="h-4 w-4 text-white" />}
                  </span>
                  {t(theme.label)}
                </Button>
              ) : (
                <Skeleton className="h-8 w-full" key={theme.name} />
              );
            })}
          </div>
        </div>
        <div className="space-y-1.5">
          <Label className="text-xs">{t("theme.radius")}</Label>
          <div className="grid grid-cols-5 gap-2">
            {["0", "0.3", "0.5", "0.75", "1.0"].map((value) => {
              return (
                <Button
                  variant={"outline"}
                  size="sm"
                  key={value}
                  onClick={() => setConfig({ ...config, radius: parseFloat(value) })}
                  className={cn(config.radius === parseFloat(value) && "border-2 border-primary")}
                >
                  {value}
                </Button>
              );
            })}
          </div>
        </div>
        <div className="space-y-1.5">
          <Label className="text-xs">{t("theme.mode.")}</Label>
          <div className="grid grid-cols-2 gap-2">
            {mounted ? (
              <>
                <Button
                  variant={"outline"}
                  size="sm"
                  onClick={() => setConfig({ ...config, mode: "light" })}
                  className={cn(config.mode === "light" && "border-2 border-primary")}
                >
                  <SunIcon className="mr-1 -translate-x-1" />
                  {t("theme.mode.light")}
                </Button>
                <Button
                  variant={"outline"}
                  size="sm"
                  onClick={() => setConfig({ ...config, mode: "dark" })}
                  className={cn(config.mode === "dark" && "border-2 border-primary")}
                >
                  <MoonIcon className="mr-1 -translate-x-1" />
                  {t("theme.mode.dark")}
                </Button>
              </>
            ) : (
              <>
                <Skeleton className="h-8 w-full" />
                <Skeleton className="h-8 w-full" />
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
