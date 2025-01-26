import { getLanguageName, LANGUAGE_DEFS } from "@helpers/i18n/language.names.ts";
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
        <Button variant="outline" className="border-none px-2 py-1">
          <div className="flex items-center gap-1">
            <GlobeIcon />
            {getLanguageName(i18n.language)}
          </div>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        {LANGUAGE_DEFS.map(({ tag, name }) => (
          <DropdownMenuItem key={tag} onClick={() => setLanguage(tag)}>
            {name}
            {i18n.language === tag && <CheckIcon size={14} className="ml-auto" />}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
