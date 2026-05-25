import { emitter } from "@helpers/event/emitter.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { act, render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MemoryRouter } from "react-router";
import { afterEach, describe, expect, it, vi } from "vitest";
import { UserNav } from "@/components/layout/user.nav.tsx";

const loadCurrent = vi.fn();
const logout = vi.fn();

vi.mock("@/sys/store.context.tsx", () => ({
  useAuthStore: () => ({
    loadCurrent,
    nickname: "Admin",
    username: "admin@example.com",
  }),
}));

vi.mock("@/sys/user/user.utils.ts", () => ({
  logout: () => logout(),
}));

describe("<UserNav />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render user avatar initials when mounted", () => {
    render(
      <MemoryRouter>
        <UserNav />
      </MemoryRouter>,
    );

    expect(screen.getByText("AD")).toBeInTheDocument();
  });

  it("should show user details when menu is opened", async () => {
    render(
      <MemoryRouter>
        <UserNav />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("button"));
    const menu = await screen.findByRole("menu");

    expect(menu).toHaveTextContent("Admin");
    expect(menu).toHaveTextContent("admin@example.com");
  });

  it("should refresh user and emit ok when reload succeeds", async () => {
    loadCurrent.mockResolvedValueOnce(true);
    const emitSpy = vi.spyOn(emitter, "emit");

    render(
      <MemoryRouter>
        <UserNav />
      </MemoryRouter>,
    );
    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(await screen.findByText("user.nav.refresh"));

    await act(async () => {
      await Promise.resolve();
    });

    expect(loadCurrent).toHaveBeenCalledOnce();
    expect(emitSpy).toHaveBeenCalledWith("error", ErrorCode.OK);
    emitSpy.mockRestore();
  });

  it("should call logout when user selects sign out", async () => {
    render(
      <MemoryRouter>
        <UserNav />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(await screen.findByText("user.nav.signOut"));

    expect(logout).toHaveBeenCalledOnce();
  });
});
