import { some } from "lodash";
import { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { matchPath } from "react-router";
import { Collapsible } from "@/components/shadcn/collapsible.tsx";
import { CollapsibleMenuItem } from "@/components/sidebar/collapsible.menu.item.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";

type Props = {
  menu: MenuDef;
};

export const CollapsibleMenu: FC<Props> = ({ menu }) => {
  const { t } = useTranslation();

  const urls = [...(menu.items?.map((x) => x.url) || []), menu.url];
  const [open, setOpen] = useState(some(urls, (x) => matchPath(x, window.location.pathname)));

  return (
    <Collapsible key={t(menu.title)} asChild onOpenChange={(x) => setOpen(x)} open={open}>
      <CollapsibleMenuItem menu={menu} />
    </Collapsible>
  );
};
