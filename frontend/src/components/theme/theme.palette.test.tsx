import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { ThemePalette } from "@/components/theme/theme.palette.tsx";
import { ConfigProvider } from "@/hooks/use.config.tsx";

describe("<ThemePalette />", () => {
  afterEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("should render theme controls when mounted", async () => {
    render(
      <ConfigProvider storageKey="theme-palette-test">
        <ThemePalette />
      </ConfigProvider>,
    );

    expect(await screen.findByText("theme.title.")).toBeInTheDocument();
    expect(screen.getByText("theme.color.")).toBeInTheDocument();
    expect(screen.getByText("theme.radius")).toBeInTheDocument();
    expect(screen.getByText("theme.mode.")).toBeInTheDocument();
  });

  it("should update theme color when user selects a palette", async () => {
    render(
      <ConfigProvider storageKey="theme-palette-test">
        <ThemePalette />
      </ConfigProvider>,
    );

    await screen.findByText("theme.color.red");
    await userEvent.click(screen.getByText("theme.color.red"));

    expect(localStorage.getItem("theme-palette-test")).toContain('"theme":"red"');
  });

  it("should update radius when user selects a value", async () => {
    render(
      <ConfigProvider storageKey="theme-palette-test">
        <ThemePalette />
      </ConfigProvider>,
    );

    await screen.findByText("0.75");
    await userEvent.click(screen.getByText("0.75"));

    expect(localStorage.getItem("theme-palette-test")).toContain('"radius":0.75');
  });

  it("should update mode when user selects dark theme", async () => {
    render(
      <ConfigProvider storageKey="theme-palette-test">
        <ThemePalette />
      </ConfigProvider>,
    );

    await screen.findByText("theme.mode.dark");
    await userEvent.click(screen.getByText("theme.mode.dark"));

    expect(localStorage.getItem("theme-palette-test")).toContain('"mode":"dark"');
  });

  it("should reset config when user clicks reset", async () => {
    render(
      <ConfigProvider
        storageKey="theme-palette-test"
        defaultConfig={{ mode: "dark", radius: 1, theme: "blue" }}
      >
        <ThemePalette />
      </ConfigProvider>,
    );

    await screen.findByText("theme.mode.light");
    await userEvent.click(screen.getByText("theme.color.red"));
    const buttons = screen.getAllByRole("button");
    await userEvent.click(buttons[0]);

    const stored = localStorage.getItem("theme-palette-test");
    expect(stored).toContain('"theme":"zinc"');
    expect(stored).toContain('"mode":"light"');
  });
});
