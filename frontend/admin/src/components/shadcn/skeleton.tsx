import { HTMLAttributes, ReactNode } from "react";
import { cn } from "@/helpers/lib/cn";

function Skeleton({ className, ...props }: HTMLAttributes<HTMLDivElement>): ReactNode {
  return <div className={cn("animate-pulse rounded-md bg-muted", className)} {...props} />;
}

export { Skeleton };
