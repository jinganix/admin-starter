import { always } from "@helpers/condition/cond.utils.ts";
import { FC, ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { CondComponent } from "@/components/condition/cond.component.tsx";
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  useSidebar,
} from "@/components/shadcn/sidebar";
import { CollapsibleMenu } from "@/components/sidebar/collapsible.menu.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { NavMenuItem } from "@/components/sidebar/nav.menu.item.tsx";
import { SidebarDropdownMenu } from "@/components/sidebar/sidebar.dropdown.menu.tsx";

function menuItem(state: "expanded" | "collapsed", menu: MenuDef): ReactNode {
  if (!menu.items?.length) {
    return <NavMenuItem menu={menu} />;
  }
  if (state === "collapsed") {
    return <SidebarDropdownMenu menu={menu} />;
  }
  return <CollapsibleMenu menu={menu} />;
}

type Props = {
  menus: MenuDef[];
};

export const NavMenu: FC<Props> = ({ menus }) => {
  const { t } = useTranslation();
  const { state } = useSidebar();

  return (
    <SidebarGroup>
      <SidebarGroupLabel>Admin Starter</SidebarGroupLabel>
      <SidebarMenu>
        {menus.map((menu) => (
          <CondComponent key={t(menu.title)} cond={menu.visible ?? always()}>
            {menuItem(state, menu)}
          </CondComponent>
        ))}
      </SidebarMenu>
    </SidebarGroup>
  );
};
