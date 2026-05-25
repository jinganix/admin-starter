import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { SettingsContainer } from "@/pages/settings/settings.container.tsx";

describe("<SettingsContainer />", () => {
  it("should render title description and children", () => {
    render(
      <SettingsContainer title="settings.profile." desc="settings.profile.description">
        <button type="button">save profile</button>
      </SettingsContainer>,
    );

    expect(
      screen.getByRole("heading", { level: 3, name: "settings.profile." }),
    ).toBeInTheDocument();
    expect(screen.getByText("settings.profile.description")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "save profile" })).toBeInTheDocument();
  });
});
