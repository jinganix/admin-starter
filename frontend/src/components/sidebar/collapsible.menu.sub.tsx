import { FC } from "react";
import { useTranslation } from "react-i18next";
import { NavLink, useMatch } from "react-router";
import {
  SidebarMenuSubButton,
  SidebarMenuSubItem,
  useSidebar,
} from "@/components/shadcn/sidebar.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { useCondStore } from "@/sys/store.context.tsx";

type Props = {
  menu: MenuDef;
};

export const CollapsibleMenuSub: FC<Props> = ({ menu }) => {
  const condStore = useCondStore();
  const { t } = useTranslation();
  const active = !!useMatch(menu.url);
  const { setOpenMobile } = useSidebar();

  if (!condStore.satisfy(menu.visible)) {
    return null;
  }

  return (
    <SidebarMenuSubItem key={t(menu.title)}>
      <SidebarMenuSubButton asChild isActive={active}>
        <NavLink to={menu.url} onClick={() => setOpenMobile(false)}>
          {menu.icon && <menu.icon />}
          <span>{t(menu.title)}</span>
        </NavLink>
      </SidebarMenuSubButton>
    </SidebarMenuSubItem>
  );
};
