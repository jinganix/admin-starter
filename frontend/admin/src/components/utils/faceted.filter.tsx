import { CheckIcon, PlusCircleIcon } from "lucide-react";
import { ComponentType, ReactNode } from "react";

import { Badge } from "@/components/shadcn/badge";
import { Button } from "@/components/shadcn/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from "@/components/shadcn/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/shadcn/popover";
import { ScrollArea } from "@/components/shadcn/scroll-area.tsx";
import { Separator } from "@/components/shadcn/separator";
import { cn } from "@/helpers/lib/cn";

interface Props<TValue> {
  title?: string;
  selected?: TValue[];
  setSelected: (values?: TValue[]) => void;
  options: {
    label: string;
    value: TValue;
    icon?: ComponentType<{ className?: string }>;
  }[];
  className?: string;
  maxShowed?: number;
}

export function FacetedFilter<TValue>({
  title,
  options,
  setSelected,
  selected,
  className,
  maxShowed,
}: Props<TValue>): ReactNode {
  const selectedValues = new Set(selected || []);

  return (
    <Popover modal>
      <PopoverTrigger asChild>
        <Button variant="outline" className={cn("h-full border-dashed", className)}>
          <PlusCircleIcon />
          {title}
          {selectedValues?.size > 0 && (
            <div className="flex items-start">
              {title && <Separator orientation="vertical" className="mx-2 h-6" />}
              {selectedValues.size > (maxShowed ?? 2) ? (
                <Badge variant="secondary" className="rounded-sm px-1 font-normal">
                  {selectedValues.size} selected
                </Badge>
              ) : (
                <div className="flex flex-wrap gap-2">
                  {options
                    .filter((option) => selectedValues.has(option.value))
                    .map((option) => (
                      <Badge
                        variant="secondary"
                        key={`${String(option.value)}`}
                        className="rounded-sm px-1 font-normal"
                      >
                        {option.label}
                      </Badge>
                    ))}
                </div>
              )}
            </div>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="min-w-48 p-0" align="start">
        <Command>
          <CommandInput placeholder={title} />
          <CommandList>
            <CommandEmpty>No results found.</CommandEmpty>
            <ScrollArea className="h-64">
              <CommandGroup>
                {options.map((option) => {
                  const isSelected = selectedValues.has(option.value);
                  return (
                    <CommandItem
                      key={`${String(option.value)}`}
                      onSelect={() => {
                        if (isSelected) {
                          selectedValues.delete(option.value);
                        } else {
                          selectedValues.add(option.value);
                        }
                        const filterValues = Array.from(selectedValues);
                        setSelected(filterValues.length ? filterValues : undefined);
                      }}
                    >
                      <div
                        className={cn(
                          "mr-2 flex h-4 w-4 items-center justify-center rounded-sm border border-primary",
                          isSelected
                            ? "bg-primary text-primary-foreground"
                            : "opacity-50 [&_svg]:invisible",
                        )}
                      >
                        <CheckIcon />
                      </div>
                      {option.icon && (
                        <option.icon className="mr-2 h-4 w-4 text-muted-foreground" />
                      )}
                      <span>{option.label}</span>
                    </CommandItem>
                  );
                })}
              </CommandGroup>
            </ScrollArea>
            {selectedValues.size > 0 && (
              <>
                <CommandSeparator />
                <CommandGroup>
                  <CommandItem
                    onSelect={() => setSelected(undefined)}
                    className="justify-center text-center"
                  >
                    Clear filters
                  </CommandItem>
                </CommandGroup>
              </>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
