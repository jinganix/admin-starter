import { ReactNode, createContext, useContext, useSyncExternalStore } from "react";
import { overviewsStore, OverviewsStore } from "@/adm/overview/overviews.store.ts";
import { authStore, AuthStore } from "@/sys/auth/auth.store.ts";
import { condStore, CondStore } from "@/sys/cond.store.ts";

type Stores = {
  authStore: AuthStore;
  condStore: CondStore;
  overviewsStore: OverviewsStore;
};

const stores: Stores = {
  authStore,
  condStore,
  overviewsStore,
};

const StoreContext = createContext<Stores>(stores);

type SubscribableStore = {
  getVersion: () => number;
  subscribe: (listener: () => void) => () => void;
};

function useStoreSubscription(store: SubscribableStore): void {
  useSyncExternalStore(store.subscribe.bind(store), store.getVersion.bind(store));
}

export function StoreProvider({ children }: { children: ReactNode }): ReactNode {
  return <StoreContext.Provider value={stores}>{children}</StoreContext.Provider>;
}

export function useAuthStore(): AuthStore {
  const value = useContext(StoreContext).authStore;
  useStoreSubscription(value);
  return value;
}

export function useCondStore(): CondStore {
  const context = useContext(StoreContext);
  useStoreSubscription(context.authStore);
  return context.condStore;
}

export function useOverviewsStore(): OverviewsStore {
  const value = useContext(StoreContext).overviewsStore;
  useStoreSubscription(value);
  return value;
}
