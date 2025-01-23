import { DropdownMenuTrigger } from "@radix-ui/react-dropdown-menu";
import { PaletteIcon } from "lucide-react";
import { ReactNode } from "react";
import { Button } from "@/components/shadcn/button.tsx";
import { DropdownMenu, DropdownMenuContent } from "@/components/shadcn/dropdown-menu.tsx";
import { ThemePalette } from "@/components/theme/theme.palette.tsx";

export function ThemeCustomizer(): ReactNode {
  return (
    <div className="flex items-center gap-2">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" size="icon" className="rounded-full">
            <PaletteIcon />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <ThemePalette />
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
