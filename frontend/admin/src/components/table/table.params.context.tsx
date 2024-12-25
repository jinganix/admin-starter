import { isEqual } from "lodash";
import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { useSearchParams } from "react-router";

type ContextValue = [URLSearchParams, (data: URLSearchParams) => void, URLSearchParams];

const params = new URLSearchParams();
const TableParamsContext = createContext<ContextValue>([params, () => {}, params]);

type Props = {
  children: ReactNode;
  value?: URLSearchParams;
};

function equals(a: URLSearchParams, b: URLSearchParams): boolean {
  return isEqual(Object.fromEntries(a.entries()), Object.fromEntries(b.entries()));
}

export function TableParamsProvider({ children, value = new URLSearchParams() }: Props): ReactNode {
  const [searchParams, setSearchParams] = useSearchParams();
  const [tableParams, setTableParams] = useState(searchParams.size > 0 ? searchParams : value);

  useEffect(() => void (!searchParams.size && setSearchParams(tableParams)), [searchParams]);

  function checkSetTableParams(params: URLSearchParams): void {
    if (!equals(params, tableParams)) {
      setTableParams(params);
      setSearchParams(params);
    }
  }

  return (
    <TableParamsContext.Provider value={[tableParams, checkSetTableParams, value]}>
      {children}
    </TableParamsContext.Provider>
  );
}

export const useTableParams = (): ContextValue => useContext(TableParamsContext);
