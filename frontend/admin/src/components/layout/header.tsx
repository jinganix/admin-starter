import { cn } from "@helpers/lib/cn.ts";
import { FC, HTMLAttributes, Ref } from "react";
import { useWindowScroll } from "react-use";
import { SidebarTrigger } from "@/components/shadcn/sidebar.tsx";

interface HeaderProps extends HTMLAttributes<HTMLElement> {
  fixed?: boolean;
  ref?: Ref<HTMLElement>;
}

export const Header: FC<HeaderProps> = ({ className, fixed, children, ...props }) => {
  const { y: offset } = useWindowScroll();

  return (
    <header
      className={cn(
        "flex items-center gap-3 sm:gap-4 bg-background p-4 h-16",
        fixed && "fixed-header peer/header w-[inherit] fixed z-10 border-b",
        offset > 10 && fixed ? "drop-shadow-md" : "shadow-none",
        className,
      )}
      {...props}
    >
      <SidebarTrigger />
      {children}
    </header>
  );
};

Header.displayName = "Header";
