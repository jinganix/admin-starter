import { cn } from "@helpers/lib/cn.ts";
import { HTMLAttributes, type JSX, ReactNode, useState } from "react";
import { Link } from "react-router";
import { useLocation, useNavigate } from "react-router-dom";
import { buttonVariants } from "@/components/shadcn/button.tsx";
import { ScrollArea } from "@/components/shadcn/scroll-area.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/shadcn/select.tsx";

interface SidebarNavProps extends HTMLAttributes<HTMLElement> {
  items: {
    href: string;
    title: string;
    icon: JSX.Element;
  }[];
}

export function SidebarNav({ className, items, ...props }: SidebarNavProps): ReactNode {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const [val, setVal] = useState(pathname ?? "/settings");

  const handleSelect = (x: string): void => {
    setVal(x);
    navigate(x);
  };

  return (
    <>
      <div className="p-1 md:hidden">
        <Select value={val} onValueChange={handleSelect}>
          <SelectTrigger className="h-12 sm:w-48">
            <SelectValue placeholder="Theme" />
          </SelectTrigger>
          <SelectContent>
            {items.map((item) => (
              <SelectItem key={item.href} value={item.href}>
                <div className="flex gap-x-4 px-2 py-1">
                  <span className="scale-125">{item.icon}</span>
                  <span className="text-md">{item.title}</span>
                </div>
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <ScrollArea type="always" className="hidden w-full bg-background px-1 py-2 md:block min-w-40">
        <nav
          className={cn("flex py-1 space-x-2 lg:flex-col lg:space-x-0 lg:space-y-1", className)}
          {...props}
        >
          {items.map((item) => (
            <Link
              key={item.href}
              to={item.href}
              className={cn(
                buttonVariants({ variant: "ghost" }),
                pathname === item.href
                  ? "bg-muted hover:bg-muted"
                  : "hover:bg-transparent hover:underline",
                "justify-start",
              )}
            >
              <span className="mr-2">{item.icon}</span>
              {item.title}
            </Link>
          ))}
        </nav>
      </ScrollArea>
    </>
  );
}
