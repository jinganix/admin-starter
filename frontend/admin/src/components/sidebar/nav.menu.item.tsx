import { FC } from "react";
import { useTranslation } from "react-i18next";
import { NavLink, useMatch } from "react-router";
import { SidebarMenuButton, SidebarMenuItem, useSidebar } from "@/components/shadcn/sidebar.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";

type Props = {
  menu: MenuDef;
};

export const NavMenuItem: FC<Props> = ({ menu }) => {
  const { t } = useTranslation();
  const active = !!useMatch(menu.url);
  const { setOpenMobile } = useSidebar();

  return (
    <SidebarMenuItem>
      <SidebarMenuButton asChild tooltip={t(menu.title)} isActive={active}>
        <NavLink to={menu.url} onClick={() => setOpenMobile(false)}>
          {menu.icon && <menu.icon />}
          <span>{t(menu.title)}</span>
        </NavLink>
      </SidebarMenuButton>
    </SidebarMenuItem>
  );
};
