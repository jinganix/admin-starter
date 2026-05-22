import { ReactNode } from "react";
import { cn } from "@/helpers/lib/cn";

function Skeleton({ className, ...props }: React.ComponentProps<"div">): ReactNode {
  return (
    <div
      data-slot="skeleton"
      className={cn("bg-accent animate-pulse rounded-md", className)}
      {...props}
    />
  );
}

export { Skeleton };
