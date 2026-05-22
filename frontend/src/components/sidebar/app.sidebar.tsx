import { ComponentProps, FC } from "react";
import { Sidebar, SidebarContent } from "@/components/shadcn/sidebar";
import { getMenuDefs } from "@/components/sidebar/menus.tsx";
import { NavMenu } from "@/components/sidebar/nav.menu.tsx";

type Props = ComponentProps<typeof Sidebar>;

export const AppSidebar: FC<Props> = ({ ...props }) => {
  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarContent>
        <NavMenu menus={getMenuDefs()} />
      </SidebarContent>
    </Sidebar>
  );
};
