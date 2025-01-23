import { cn } from "@helpers/lib/cn.ts";
import { CheckIcon, GlobeIcon } from "lucide-react";
import { ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu";

type Props = {
  className?: string;
};

export function LanguageSwitch({ className }: Props): ReactNode {
  const { i18n } = useTranslation();
  const setLanguage = async (lang: string): Promise<void> => {
    await i18n.changeLanguage(lang);
  };

  return (
    <DropdownMenu modal={false}>
      <DropdownMenuTrigger asChild className={cn(className)}>
        <Button variant="outline" className="border-none h-8 px-2 py-1">
          <div className="flex items-center gap-1">
            <GlobeIcon />
            {i18n.language.toUpperCase()}
          </div>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={() => setLanguage("en")}>
          English {i18n.language === "en" && <CheckIcon size={14} className="ml-auto" />}
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => setLanguage("zh")}>
          中文
          {i18n.language === "zh" && <CheckIcon size={14} className="ml-auto" />}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
