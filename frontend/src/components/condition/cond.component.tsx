import { observer } from "mobx-react-lite";
import { FC, ReactNode } from "react";
import { Cond } from "@/helpers/condition/cond.types.ts";
import { condStore } from "@/sys/cond.store.ts";

type Props = {
  cond: Cond;
  children: ReactNode;
};

export const CondComponent: FC<Props> = observer(({ cond, children }) => {
  return condStore.satisfy(cond) ? children : null;
});
