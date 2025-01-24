import { includes } from "lodash";
import { MinusIcon, PlusIcon, XIcon } from "lucide-react";
import { ReactNode, useEffect, useState } from "react";
import { useResizeDetector } from "react-resize-detector";
import { Button } from "@/components/shadcn/button.tsx";
import { Input } from "@/components/shadcn/input.tsx";
import { TreeItem, TreeViewItem } from "@/components/tree/tree.view.item.tsx";
import { useTreeState } from "@/components/tree/use.tree.state.tsx";
import { cn } from "@/helpers/lib/cn";

type Props<T> = {
  title: string;
  items: TreeItem<T>[];
  className: string;
  selected?: T[];
  setSelected: (values?: T[]) => void;
};

function filter<T>(items: TreeItem<T>[] = [], keyword: string): TreeItem<T>[] {
  return items
    .map((x) => {
      if (!keyword) {
        return x;
      }
      if (includes(x.label.toUpperCase(), keyword.toUpperCase())) {
        return x;
      }
      const filtered = filter(x.items, keyword);
      return filtered.length ? { ...x, items: filtered } : null;
    })
    .filter((x) => x != null);
}

export function TreeView<T>({
  title,
  items,
  className,
  selected,
  setSelected,
}: Props<T>): ReactNode {
  const [keyword, setKeyword] = useState("");
  const [treeItems, setTreeItems] = useState(items);
  const [_, setState] = useTreeState();
  const detector = useResizeDetector();

  useEffect(() => setTreeItems(items), [items]);
  useEffect(() => setTreeItems(filter(items, keyword)), [keyword]);

  return (
    <div className={cn("space-y-2", className)}>
      <div ref={detector.ref} className="w-full flex items-center space-x-2">
        <div className="relative flex-1">
          <Input
            className=""
            placeholder={title}
            value={keyword}
            onChange={(x) => setKeyword(x.target.value)}
          />
          <Button
            type="button"
            variant="ghost"
            size="icon"
            className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
            onClick={() => setKeyword("")}
          >
            <XIcon className="h-4 w-4" />
          </Button>
        </div>

        <Button type="button" variant="outline" size="icon" onClick={() => setState("collapsed")}>
          <MinusIcon />
        </Button>

        <Button type="button" variant="outline" size="icon" onClick={() => setState("expanded")}>
          <PlusIcon />
        </Button>
      </div>
      <div className="space-y-2 px-2 overflow-x-scroll" style={{ width: detector.width }}>
        {treeItems.map((x) => (
          <TreeViewItem
            key={String(x.value)}
            item={x}
            selected={selected}
            setSelected={setSelected}
          />
        ))}
      </div>
    </div>
  );
}
