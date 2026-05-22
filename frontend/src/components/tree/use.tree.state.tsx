import { createContext, ReactNode, useContext, useState } from "react";

export type TreeState = "expanded" | "collapsed" | "partially";

const TreeStateContext = createContext<TreeStateValue>(["partially", () => {}]);

type TreeStateProps = {
  children: ReactNode;
  state?: TreeState;
};

type TreeStateValue = [TreeState, (state: TreeState) => void];

export function TreeStateProvider({
  children,
  state: defaultState = "partially",
  ...props
}: TreeStateProps): ReactNode {
  const [state, setState] = useState<TreeState>(defaultState);

  return (
    <TreeStateContext.Provider {...props} value={[state, setState]}>
      {children}
    </TreeStateContext.Provider>
  );
}

export const useTreeState = (): [TreeState, (state: TreeState) => void] => {
  const context = useContext(TreeStateContext);
  if (context === undefined) {
    throw new Error("useTreeState must be used within a TreeStateProvider");
  }
  return context;
};
