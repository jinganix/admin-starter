import { cn } from "@helpers/lib/cn.ts";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Link, useMatch } from "react-router";
import { DropdownMenuItem } from "@/components/shadcn/dropdown-menu";
import { MenuDef } from "@/components/sidebar/menus.tsx";

type Props = {
  menu: MenuDef;
};

export const SidebarDropdownSub: FC<Props> = ({ menu }) => {
  const { t } = useTranslation();
  const active = !!useMatch(menu.url);

  return (
    <DropdownMenuItem asChild>
      <Link to={menu.url} className={cn({ "bg-primary/20": active })}>
        {menu.icon && <menu.icon />}
        <span className="max-w-52 text-wrap">{t(menu.title)}</span>
      </Link>
    </DropdownMenuItem>
  );
};
