import i18next from "i18next";
import { EditIcon, MoreHorizontal, Trash2Icon } from "lucide-react";
import { ReactNode } from "react";
import { Button } from "@/components/shadcn/button.tsx";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu.tsx";

export type RowAction<Item, Types = undefined> = {
  type: Types extends undefined
    ? "delete" | "edit" | "create"
    : "delete" | "edit" | "create" | Types;
  item?: Item;
};

type Props<T> = {
  item: T;
  setAction: (action: RowAction<T>) => void;
  children?: ReactNode;
};

export function RowActions<T>({ item, setAction, children }: Props<T>): ReactNode {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="h-6 w-8 p-0">
          <MoreHorizontal />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        {children}

        <DropdownMenuItem onClick={() => setAction({ item, type: "edit" })}>
          {i18next.t("action.edit")}
          <DropdownMenuShortcut>
            <EditIcon size={16} />
          </DropdownMenuShortcut>
        </DropdownMenuItem>

        <DropdownMenuSeparator />

        <DropdownMenuItem
          className="!text-red-500"
          onClick={() => setAction({ item, type: "delete" })}
        >
          {i18next.t("action.delete")}
          <DropdownMenuShortcut>
            <Trash2Icon size={16} />
          </DropdownMenuShortcut>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
