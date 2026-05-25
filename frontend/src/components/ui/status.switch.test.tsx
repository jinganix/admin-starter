import { act, render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { StatusSwitch } from "@/components/ui/status.switch.tsx";

vi.mock("i18next", () => ({
  default: {
    t: (key: string) => key,
  },
}));

describe("<StatusSwitch />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render switch label when mounted", () => {
    render(<StatusSwitch checked={true} i18nKey="status.active" onCheckedChange={vi.fn()} />);

    expect(screen.getByText("status.active")).toBeInTheDocument();
  });

  it("should call onCheckedChange when user toggles switch", async () => {
    const onCheckedChange = vi.fn().mockResolvedValue(undefined);

    render(
      <StatusSwitch checked={false} i18nKey="status.active" onCheckedChange={onCheckedChange} />,
    );

    await userEvent.click(screen.getByRole("switch"));

    await act(async () => {
      await Promise.resolve();
    });

    expect(onCheckedChange).toHaveBeenCalledOnce();
  });
});
