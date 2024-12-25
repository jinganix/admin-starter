import { ChevronRightIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { CollapsibleContent, CollapsibleTrigger } from "@/components/shadcn/collapsible.tsx";
import {
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
} from "@/components/shadcn/sidebar.tsx";
import { CollapsibleMenuSub } from "@/components/sidebar/collapsible.menu.sub.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";

type Props = {
  menu: MenuDef;
};

export const CollapsibleMenuItem: FC<Props> = ({ menu }) => {
  const { t } = useTranslation();

  return (
    <SidebarMenuItem>
      <CollapsibleTrigger asChild>
        <SidebarMenuButton tooltip={t(menu.title)}>
          {menu.icon && <menu.icon />}
          <span>{t(menu.title)}</span>
          <ChevronRightIcon className="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
        </SidebarMenuButton>
      </CollapsibleTrigger>
      <CollapsibleContent>
        <SidebarMenuSub>
          {menu.items?.map((sub) => <CollapsibleMenuSub key={sub.title} menu={sub} />)}
        </SidebarMenuSub>
      </CollapsibleContent>
    </SidebarMenuItem>
  );
};
