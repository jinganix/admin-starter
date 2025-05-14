import { FC, HTMLAttributes, Ref } from "react";
import { cn } from "@/helpers/lib/cn";

interface MainProps extends HTMLAttributes<HTMLElement> {
  fixed?: boolean;
  ref?: Ref<HTMLElement>;
}

export const LayoutContent: FC<MainProps> = ({ fixed, className, ...props }) => {
  return (
    <main
      className={cn(
        "peer-[.fixed-header]/header:mt-16",
        fixed && "fixed-main flex flex-col grow overflow-hidden",
        className,
      )}
      {...props}
    />
  );
};

LayoutContent.displayName = "LayoutContent";
