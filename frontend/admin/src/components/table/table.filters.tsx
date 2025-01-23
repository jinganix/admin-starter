import { cn } from "@helpers/lib/cn.ts";
import { DropdownMenuTrigger } from "@radix-ui/react-dropdown-menu";
import { FilterIcon } from "lucide-react";
import { FC, HTMLAttributes } from "react";

import { useTranslation } from "react-i18next";
import { useMediaQuery } from "react-responsive";
import { Button } from "@/components/shadcn/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
} from "@/components/shadcn/dropdown-menu.tsx";

type Props = HTMLAttributes<HTMLDivElement>;

export const TableFilters: FC<Props> = ({ className, children }) => {
  const { t } = useTranslation();
  const xl = useMediaQuery({ query: "(min-width: 1280px)" });

  return (
    <>
      {!xl && (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm" className={cn("h-8", className)}>
              <FilterIcon />
              <span className="hidden lg:block">{t("table.filter.")}</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuLabel>{t("table.filter.header")}</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <div className="p-4 max-w-sm">{children}</div>
          </DropdownMenuContent>
        </DropdownMenu>
      )}
      {xl && children}
    </>
  );
};
