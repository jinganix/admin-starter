import { CheckedState } from "@radix-ui/react-checkbox";
import { groupBy } from "lodash";
import { ChevronDownIcon, ChevronRightIcon } from "lucide-react";
import { ReactNode, useState } from "react";
import { Button } from "@/components/shadcn/button.tsx";
import { Checkbox } from "@/components/shadcn/checkbox.tsx";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/shadcn/collapsible.tsx";
import { cn } from "@/helpers/lib/cn";

export interface TreeItem<T> {
  label: string;
  value: T;
  code: string;
  items?: TreeItem<T>[];
}

type Props<T> = {
  item: TreeItem<T>;
  level?: number;
  selected?: T[];
  setSelected: (values?: T[]) => void;
};

export function TreeViewItem<T>({ item, level = 1, setSelected, selected }: Props<T>): ReactNode {
  const [open, setOpen] = useState(false);
  const selectedValues = new Set(selected || []);

  function checkedState(item: TreeItem<T>): CheckedState {
    if (!item.items?.length) {
      return selectedValues.has(item.value);
    }
    const data = groupBy(item.items, (x) => checkedState(x));
    if (data["indeterminate"]) {
      return "indeterminate";
    } else if (data["true"] && !data["false"]) {
      return true;
    } else if (data["false"] && !data["true"]) {
      return false;
    }
    return "indeterminate";
  }

  const isSelected = checkedState(item);

  function onCheckedAll(checked: string | boolean, item: TreeItem<T>): void {
    if (checked) {
      selectedValues.add(item.value);
    } else {
      selectedValues.delete(item.value);
    }
    item.items?.forEach((x) => onCheckedAll(checked, x));
  }

  const onChecked = (checked: string | boolean): void => {
    onCheckedAll(checked, item);
    const filterValues = Array.from(selectedValues);
    setSelected(filterValues.length ? filterValues : undefined);
  };

  return (
    <>
      {!item.items && (
        <div className={cn("flex items-center space-x-2", { "pl-4 ml-2 border-l": level > 1 })}>
          {item.value && (
            <Checkbox id={item.code} checked={isSelected} onCheckedChange={(x) => onChecked(x)} />
          )}
          <label
            htmlFor={item.code}
            className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
          >
            <span className="flex space-x-1 text-sm whitespace-nowrap">
              <span className="font-semibold">{item.label}</span>
              <span className="opacity-60">({item.code})</span>
            </span>
          </label>
        </div>
      )}
      {item.items && (
        <Collapsible
          open={open}
          onOpenChange={setOpen}
          className={cn("space-y-2", { "pl-4 ml-2 border-l": level > 1 })}
        >
          <div className="flex items-center gap-2">
            <Checkbox id="item" checked={isSelected} onCheckedChange={(x) => onChecked(x)} />
            <CollapsibleTrigger asChild>
              <Button variant="ghost" size="sm" className="p-0 h-auto">
                {open ? (
                  <ChevronDownIcon className="h-4 w-4" />
                ) : (
                  <ChevronRightIcon className="h-4 w-4" />
                )}
                <span className="flex space-x-1 text-sm whitespace-nowrap">
                  <span className="font-semibold">{item.label}</span>
                  <span className="opacity-60">({item.code})</span>
                </span>
              </Button>
            </CollapsibleTrigger>
          </div>
          <CollapsibleContent className="space-y-2">
            {item.items.map((x) => (
              <TreeViewItem
                key={x.label}
                item={x}
                level={level + 1}
                selected={selected}
                setSelected={setSelected}
              />
            ))}
          </CollapsibleContent>
        </Collapsible>
      )}
    </>
  );
}
