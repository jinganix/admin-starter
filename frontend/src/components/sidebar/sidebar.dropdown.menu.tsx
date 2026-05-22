import { ChevronRightIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { useMatch } from "react-router";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu";
import { SidebarMenuButton, SidebarMenuItem } from "@/components/shadcn/sidebar";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { SidebarDropdownSub } from "@/components/sidebar/sidebar.dropdown.sub.tsx";

type Props = {
  menu: MenuDef;
};

export const SidebarDropdownMenu: FC<Props> = ({ menu }) => {
  const { t } = useTranslation();
  const active = !!useMatch(menu.url);

  return (
    <SidebarMenuItem>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <SidebarMenuButton tooltip={t(menu.title)} isActive={active}>
            {menu.icon && <menu.icon />}
            <span>{t(menu.title)}</span>
            <ChevronRightIcon className="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
          </SidebarMenuButton>
        </DropdownMenuTrigger>
        <DropdownMenuContent side="right" align="start" sideOffset={4}>
          <DropdownMenuLabel>{t(menu.title)}</DropdownMenuLabel>
          <DropdownMenuSeparator />
          {menu.items?.map((sub) => (
            <SidebarDropdownSub key={`${sub.title}-${sub.url}`} menu={sub} />
          ))}
        </DropdownMenuContent>
      </DropdownMenu>
    </SidebarMenuItem>
  );
};
