import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { emitter } from "@/helpers/event/emitter.ts";
import { SettingsProfilePage } from "@/pages/settings/settings.profile.page.tsx";
import { UserActions } from "@/sys/user/user.actions.ts";

vi.mock("@/sys/user/user.actions.ts", () => ({
  UserActions: { updateProfile: vi.fn() },
}));

vi.mock("@/sys/auth/auth.store.ts", () => ({
  authStore: { nickname: "alice" },
}));

describe("<SettingsProfilePage />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should show validation error when nickname is too short", async () => {
    render(<SettingsProfilePage />);

    const input = screen.getByPlaceholderText("settings.profile.nickname.min");
    await userEvent.clear(input);
    await userEvent.type(input, "ab");
    await userEvent.click(screen.getByRole("button", { name: "settings.profile.submit" }));

    expect(await screen.findByText("settings.profile.nickname.min")).toBeInTheDocument();
    expect(UserActions.updateProfile).not.toHaveBeenCalled();
  });

  it("should update profile and emit success when nickname is valid", async () => {
    vi.mocked(UserActions.updateProfile).mockResolvedValue(true);
    const emit = vi.spyOn(emitter, "emit");

    render(<SettingsProfilePage />);

    const input = screen.getByPlaceholderText("settings.profile.nickname.min");
    await userEvent.clear(input);
    await userEvent.type(input, "newnick");
    await userEvent.click(screen.getByRole("button", { name: "settings.profile.submit" }));

    await waitFor(() => expect(UserActions.updateProfile).toHaveBeenCalledWith("newnick"));
    expect(emit).toHaveBeenCalledWith("error", ErrorCode.OK);
  });
});
