import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MemoryRouter } from "react-router";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TokenService } from "@/helpers/network/token.service.ts";
import { LoginPage } from "@/pages/login.page.tsx";

const { auth, dispose, initialize, resolveMock } = vi.hoisted(() => ({
  auth: vi.fn(),
  dispose: vi.fn(),
  initialize: vi.fn().mockResolvedValue(undefined),
  resolveMock: vi.fn(),
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

describe("<LoginPage />", () => {
  afterEach(() => {
    vi.clearAllMocks();
    resolveMock.mockReset();
    resolveMock.mockReturnValue({ auth });
  });

  it("should show validation errors when fields are empty", async () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("button", { name: "auth.login" }));

    expect(await screen.findByText("auth.username.min")).toBeInTheDocument();
    expect(screen.getByText("auth.password.min")).toBeInTheDocument();
    expect(auth).not.toHaveBeenCalled();
  });

  it.each([
    { field: "username", message: "auth.username.min", value: "ab" },
    { field: "username", message: "auth.username.max", value: "a".repeat(21) },
    { field: "password", message: "auth.password.min", value: "12345" },
    { field: "password", message: "auth.password.max", value: "a".repeat(21) },
  ])("should show $message when $field is out of bounds", async ({ field, value, message }) => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    const username = screen.getByPlaceholderText("auth.username.");
    const password = screen.getByPlaceholderText("auth.password.");

    if (field === "username") {
      await userEvent.type(username, value);
      await userEvent.type(password, "secret12");
    } else {
      await userEvent.type(username, "validuser");
      await userEvent.type(password, value);
    }

    await userEvent.click(screen.getByRole("button", { name: "auth.login" }));

    expect(await screen.findByText(message)).toBeInTheDocument();
    expect(auth).not.toHaveBeenCalled();
  });

  it("should authenticate and initialize store when login succeeds", async () => {
    auth.mockResolvedValue(true);

    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    await userEvent.type(screen.getByPlaceholderText("auth.username."), "validuser");
    await userEvent.type(screen.getByPlaceholderText("auth.password."), "secret12");
    await userEvent.click(screen.getByRole("button", { name: "auth.login" }));

    await waitFor(() => expect(resolveMock).toHaveBeenCalledWith(TokenService));
    expect(auth).toHaveBeenCalledWith("validuser", "secret12");
    expect(dispose).toHaveBeenCalled();
    expect(initialize).toHaveBeenCalled();
  });

  it("should not initialize store when authentication fails", async () => {
    auth.mockResolvedValue(false);

    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    await userEvent.type(screen.getByPlaceholderText("auth.username."), "validuser");
    await userEvent.type(screen.getByPlaceholderText("auth.password."), "secret12");
    await userEvent.click(screen.getByRole("button", { name: "auth.login" }));

    await waitFor(() => expect(auth).toHaveBeenCalled());
    expect(dispose).not.toHaveBeenCalled();
    expect(initialize).not.toHaveBeenCalled();
  });
});
