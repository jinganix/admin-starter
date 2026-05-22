import { cn } from "@helpers/lib/cn.ts";
import { FC, HTMLProps } from "react";

type Props = HTMLProps<HTMLDivElement>;

export const TableFilterableCell: FC<Props> = ({ className, children, ...props }) => {
  return (
    <div
      className={cn(
        "font-medium text-blue-600 dark:text-blue-500 hover:underline cursor-pointer whitespace-nowrap",
        className,
      )}
      {...props}
    >
      {children}
    </div>
  );
};
