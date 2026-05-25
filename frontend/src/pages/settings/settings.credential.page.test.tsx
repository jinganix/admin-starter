import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { emitter } from "@/helpers/event/emitter.ts";
import { SettingsCredentialPage } from "@/pages/settings/settings.credential.page.tsx";
import { UserActions } from "@/sys/user/user.actions.ts";

vi.mock("@/sys/user/user.actions.ts", () => ({
  UserActions: { changePassword: vi.fn() },
}));

describe("<SettingsCredentialPage />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should show validation error when current password is too short", async () => {
    render(<SettingsCredentialPage />);

    await userEvent.type(
      screen.getByPlaceholderText("settings.credential.current.placeholder"),
      "12345",
    );
    await userEvent.type(screen.getByPlaceholderText("settings.credential.password"), "secret12");
    await userEvent.type(screen.getByPlaceholderText("settings.credential.confirm"), "secret12");
    await userEvent.click(screen.getByRole("button", { name: "action.submit" }));

    expect(await screen.findByText("auth.password.min")).toBeInTheDocument();
    expect(UserActions.changePassword).not.toHaveBeenCalled();
  });

  it("should show mismatch error when confirm password differs", async () => {
    render(<SettingsCredentialPage />);

    await userEvent.type(
      screen.getByPlaceholderText("settings.credential.current.placeholder"),
      "oldpass1",
    );
    await userEvent.type(screen.getByPlaceholderText("settings.credential.password"), "secret12");
    await userEvent.type(screen.getByPlaceholderText("settings.credential.confirm"), "secret99");
    await userEvent.click(screen.getByRole("button", { name: "action.submit" }));

    expect(await screen.findByText("auth.password.notMatch")).toBeInTheDocument();
    expect(UserActions.changePassword).not.toHaveBeenCalled();
  });

  it("should change password and emit success when form is valid", async () => {
    vi.mocked(UserActions.changePassword).mockResolvedValue(true);
    const emit = vi.spyOn(emitter, "emit");

    render(<SettingsCredentialPage />);

    await userEvent.type(
      screen.getByPlaceholderText("settings.credential.current.placeholder"),
      "oldpass1",
    );
    await userEvent.type(screen.getByPlaceholderText("settings.credential.password"), "secret12");
    await userEvent.type(screen.getByPlaceholderText("settings.credential.confirm"), "secret12");
    await userEvent.click(screen.getByRole("button", { name: "action.submit" }));

    await waitFor(() =>
      expect(UserActions.changePassword).toHaveBeenCalledWith("oldpass1", "secret12"),
    );
    expect(emit).toHaveBeenCalledWith("error", ErrorCode.OK);
  });
});
