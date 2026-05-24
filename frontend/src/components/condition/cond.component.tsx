import { FC, ReactNode } from "react";
import { Cond } from "@/helpers/condition/cond.types.ts";
import { useCondStore } from "@/sys/store.context.tsx";

type Props = {
  cond: Cond;
  children: ReactNode;
};

export const CondComponent: FC<Props> = ({ cond, children }) => {
  const condStore = useCondStore();
  return condStore.satisfy(cond) ? children : null;
};
