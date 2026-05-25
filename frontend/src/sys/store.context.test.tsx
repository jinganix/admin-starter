import { UserStatus } from "@proto/SysUserProto";
import { act, render, screen, waitFor } from "@testing-library/react";
import { container } from "tsyringe";
import { afterEach, describe, expect, it, vi } from "vitest";
import { overviewsStore } from "@/adm/overview/overviews.store.ts";
import { CondType } from "@/helpers/condition/cond.types.ts";
import { authStore } from "@/sys/auth/auth.store.ts";
import { condStore } from "@/sys/cond.store.ts";
import {
  StoreProvider,
  useAuthStore,
  useCondStore,
  useOverviewsStore,
} from "@/sys/store.context.tsx";

function AuthProbe(): React.ReactElement {
  const store = useAuthStore();
  return <span data-testid="auth-authed">{store.isAuthed() ? "yes" : "no"}</span>;
}

function CondProbe(): React.ReactElement {
  const store = useCondStore();
  return (
    <span data-testid="cond-authed">{store.satisfy({ type: CondType.authed }) ? "yes" : "no"}</span>
  );
}

function OverviewsProbe(): React.ReactElement {
  const store = useOverviewsStore();
  return <span data-testid="overview-count">{store.records.length}</span>;
}

describe("StoreProvider", () => {
  afterEach(() => {
    authStore.dispose();
  });

  it("should render children when mounted", () => {
    render(
      <StoreProvider>
        <span>child content</span>
      </StoreProvider>,
    );

    expect(screen.getByText("child content")).toBeInTheDocument();
  });

  it("should expose auth store state to consumers", () => {
    render(
      <StoreProvider>
        <AuthProbe />
      </StoreProvider>,
    );

    expect(screen.getByTestId("auth-authed")).toHaveTextContent("no");

    act(() => {
      authStore.update({
        createdAt: 0,
        id: "1",
        nickname: "n",
        status: UserStatus.ACTIVE,
        username: "u",
      });
    });

    expect(screen.getByTestId("auth-authed")).toHaveTextContent("yes");
  });

  it("should re-render cond consumer when auth store changes", () => {
    render(
      <StoreProvider>
        <CondProbe />
      </StoreProvider>,
    );

    expect(screen.getByTestId("cond-authed")).toHaveTextContent("no");

    act(() => {
      authStore.update({
        createdAt: 0,
        id: "1",
        nickname: "n",
        status: UserStatus.ACTIVE,
        username: "u",
      });
    });

    expect(screen.getByTestId("cond-authed")).toHaveTextContent("yes");
  });

  it("should expose cond store instance from hook", () => {
    let storeFromHook: typeof condStore | undefined;

    function CondStoreProbe(): null {
      storeFromHook = useCondStore();
      return null;
    }

    render(
      <StoreProvider>
        <CondStoreProbe />
      </StoreProvider>,
    );

    expect(storeFromHook).toBe(condStore);
  });

  it("should re-render overviews consumer when overviews store notifies", async () => {
    vi.spyOn(container, "resolve").mockReturnValue({
      request: vi.fn().mockResolvedValue({
        records: [
          {
            apiGet: 1,
            apiPost: 1,
            month: "2024-01-01",
            permissionCreated: 0,
            permissionDeleted: 0,
            roleCreated: 0,
            roleDeleted: 0,
            userCreated: 1,
            userDeleted: 0,
          },
        ],
      }),
    } as never);

    render(
      <StoreProvider>
        <OverviewsProbe />
      </StoreProvider>,
    );

    expect(screen.getByTestId("overview-count")).toHaveTextContent("0");

    await act(async () => {
      await overviewsStore.load();
    });

    await waitFor(() => {
      expect(screen.getByTestId("overview-count")).toHaveTextContent("1");
    });
  });

  it("should expose auth store instance from hook", () => {
    let storeFromHook: typeof authStore | undefined;

    function AuthStoreProbe(): null {
      storeFromHook = useAuthStore();
      return null;
    }

    render(
      <StoreProvider>
        <AuthStoreProbe />
      </StoreProvider>,
    );

    expect(storeFromHook).toBe(authStore);
  });

  it("should expose overviews store instance from hook", () => {
    let storeFromHook: typeof overviewsStore | undefined;

    function OverviewsStoreProbe(): null {
      storeFromHook = useOverviewsStore();
      return null;
    }

    render(
      <StoreProvider>
        <OverviewsStoreProbe />
      </StoreProvider>,
    );

    expect(storeFromHook).toBe(overviewsStore);
  });
});
