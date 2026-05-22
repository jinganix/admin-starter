import { useState } from "react";

export function useLoading<T extends Array<unknown>, U>(
  fn: (...args: T) => Promise<U>,
  defaultValue = false,
): [boolean, (...args: T) => Promise<U>] {
  const [loading, setLoading] = useState(defaultValue);

  return [
    loading,
    async (...args: T): Promise<U> => {
      setLoading(true);
      try {
        return await fn(...args);
      } finally {
        setLoading(false);
      }
    },
  ];
}
