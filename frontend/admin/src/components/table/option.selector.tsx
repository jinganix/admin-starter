import { Option } from "@helpers/option.ts";
import { PopoverProps } from "@radix-ui/react-popover";
import { CheckIcon, ChevronsUpDownIcon } from "lucide-react";
import { ReactNode, useState } from "react";
import { useTranslation } from "react-i18next";
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
import { cn } from "@/helpers/lib/cn";
import { useIsMobile } from "@/hooks/use.mobile.tsx";

interface Props<T> extends PopoverProps {
  options: Option<T>[];
  selected?: T;
  placeholder: string;
  className?: string;
  setSelected: (value?: T) => void;
}

export function OptionSelector<T>({
  selected,
  options,
  placeholder,
  className,
  setSelected,
  ...props
}: Props<T>): ReactNode {
  const { t } = useTranslation();
  const isMobile = useIsMobile();

  const [open, setOpen] = useState(false);
  const option = options.filter((x) => x.value === selected)[0];

  return (
    <Popover open={open} onOpenChange={setOpen} {...props}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-label="Load a preset..."
          aria-expanded={open}
          className={cn("flex-1 justify-between", className)}
        >
          {option ? option.label : placeholder}
          <ChevronsUpDownIcon className="opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="p-0" onOpenAutoFocus={(x) => isMobile && x.preventDefault()}>
        <Command>
          <CommandInput placeholder={placeholder} />
          <CommandList>
            <CommandEmpty>No options found.</CommandEmpty>
            <CommandGroup>
              {options.map((x) => (
                <CommandItem
                  key={`${x.value}`}
                  onSelect={() => {
                    setSelected(x.value);
                    setOpen(false);
                  }}
                >
                  {x.label}
                  <CheckIcon
                    className={cn(
                      "ml-auto",
                      option?.value === x.value ? "opacity-100" : "opacity-0",
                    )}
                  />
                </CommandItem>
              ))}
            </CommandGroup>
            {option !== undefined && (
              <>
                <CommandSeparator />
                <CommandGroup>
                  <CommandItem
                    onSelect={() => {
                      setSelected(undefined);
                      setOpen(false);
                    }}
                    className="justify-center text-center"
                  >
                    {t("option.selector.clear")}
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
