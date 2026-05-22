import { DropdownMenuTrigger } from "@radix-ui/react-dropdown-menu";
import { MenuIcon } from "lucide-react";
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

export const TableActions: FC<Props> = ({ children }) => {
  const { t } = useTranslation();
  const xl = useMediaQuery({ query: "(min-width: 1280px)" });

  return (
    <>
      {!xl && (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm" className="h-8">
              <MenuIcon />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuLabel>{t("table.action.header")}</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <div className="flex flex-col space-y-4 p-4">{children}</div>
          </DropdownMenuContent>
        </DropdownMenu>
      )}
      {xl && <div className="flex gap-4">{children}</div>}
    </>
  );
};
