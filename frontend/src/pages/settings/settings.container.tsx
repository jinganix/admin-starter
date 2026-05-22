import { ReactNode } from "react";
import { ScrollArea } from "@/components/shadcn/scroll-area.tsx";
import { Separator } from "@/components/shadcn/separator.tsx";

interface ContentSectionProps {
  title: string;
  desc: string;
  children: ReactNode;
}

export function SettingsContainer({ title, desc, children }: ContentSectionProps): ReactNode {
  return (
    <div className="flex flex-1 flex-col">
      <div className="flex-none">
        <h3 className="text-lg font-medium">{title}</h3>
        <p className="text-sm text-muted-foreground">{desc}</p>
      </div>
      <Separator className="my-4 flex-none" />
      <ScrollArea className="faded-bottom -mx-4 flex-1 scroll-smooth px-4 md:pb-16">
        <div className="lg:max-w-xl px-1.5">{children}</div>
      </ScrollArea>
    </div>
  );
}
