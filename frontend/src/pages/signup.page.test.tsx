import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MemoryRouter } from "react-router";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TokenService } from "@/helpers/network/token.service.ts";
import { SignupPage } from "@/pages/signup.page.tsx";

const { signup, dispose, initialize, resolveMock } = vi.hoisted(() => ({
  dispose: vi.fn(),
  initialize: vi.fn().mockResolvedValue(undefined),
  resolveMock: vi.fn(),
  signup: vi.fn(),
}));

vi.mock("tsyringe", async (importActual) => {
  const actual = await importActual<typeof import("tsyringe")>();
  return {
    ...actual,
    container: { ...actual.container, resolve: resolveMock },
  };
});

vi.mock("@/sys/auth/auth.store.ts", () => ({
  authStore: { dispose, initialize },
}));

describe("<SignupPage />", () => {
  afterEach(() => {
    vi.clearAllMocks();
    resolveMock.mockReset();
    resolveMock.mockReturnValue({ signup });
  });

  it("should show validation errors when required fields are empty", async () => {
    render(
      <MemoryRouter>
        <SignupPage />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("button", { name: "auth.signup" }));

    expect(await screen.findByText("auth.username.min")).toBeInTheDocument();
    expect(screen.getByText("auth.password.min")).toBeInTheDocument();
    expect(signup).not.toHaveBeenCalled();
  });

  it("should show mismatch error when passwords differ", async () => {
    render(
      <MemoryRouter>
        <SignupPage />
      </MemoryRouter>,
    );

    await userEvent.type(screen.getByPlaceholderText("auth.username."), "newuser");
    await userEvent.type(screen.getByPlaceholderText("auth.password."), "secret12");
    await userEvent.type(screen.getByPlaceholderText("auth.password.confirm"), "secret99");
    await userEvent.click(screen.getByRole("button", { name: "auth.signup" }));

    expect(await screen.findByText("auth.password.notMatch")).toBeInTheDocument();
    expect(signup).not.toHaveBeenCalled();
  });

  it("should sign up and initialize store when signup succeeds", async () => {
    signup.mockResolvedValue(true);

    render(
      <MemoryRouter>
        <SignupPage />
      </MemoryRouter>,
    );

    await userEvent.type(screen.getByPlaceholderText("auth.username."), "newuser");
    await userEvent.type(screen.getByPlaceholderText("auth.password."), "secret12");
    await userEvent.type(screen.getByPlaceholderText("auth.password.confirm"), "secret12");
    await userEvent.click(screen.getByRole("button", { name: "auth.signup" }));

    await waitFor(() => expect(resolveMock).toHaveBeenCalledWith(TokenService));
    expect(signup).toHaveBeenCalledWith("newuser", "secret12");
    expect(dispose).toHaveBeenCalled();
    expect(initialize).toHaveBeenCalled();
  });
});
