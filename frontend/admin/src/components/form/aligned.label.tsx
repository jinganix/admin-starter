import { cn } from "@helpers/lib/cn.ts";
import { makeAutoObservable } from "mobx";
import { observer } from "mobx-react-lite";
import {
  ComponentPropsWithoutRef,
  createContext,
  FC,
  ReactNode,
  useContext,
  useEffect,
} from "react";
import { useResizeDetector } from "react-resize-detector";
import { FormLabel } from "@/components/shadcn/form.tsx";

export class LabelAligner {
  width = 0;

  constructor() {
    makeAutoObservable(this);
  }

  update(width?: number): void {
    if (width && width > this.width) {
      this.width = width;
    }
  }
}

export const LabelAlignerContext = createContext<LabelAligner>(new LabelAligner());

type LabelResizerProviderProps = {
  children: ReactNode;
  aligner?: LabelAligner;
};

export const LabelAlignerProvider: FC<LabelResizerProviderProps> = ({
  children,
  aligner = new LabelAligner(),
}) => {
  return <LabelAlignerContext.Provider value={aligner}>{children}</LabelAlignerContext.Provider>;
};

export const useLabelAligner = (): LabelAligner => {
  const context = useContext(LabelAlignerContext);
  if (context === undefined) {
    throw new Error("useConfig must be used within a ConfigProvider");
  }
  return context;
};

type ResizedLabelProps = ComponentPropsWithoutRef<typeof FormLabel>;

export const AlignedLabel: FC<ResizedLabelProps> = observer(({ children, className }) => {
  const aligner = useLabelAligner();
  const { width, ref } = useResizeDetector();
  width && aligner.update(width);
  useEffect(() => void (width && aligner.update(width)), [width]);

  return (
    <FormLabel
      ref={ref}
      className={cn("min-w-12 text-right", { block: aligner.width }, className)}
      style={aligner.width ? { width: aligner.width } : {}}
    >
      {children}
    </FormLabel>
  );
});
