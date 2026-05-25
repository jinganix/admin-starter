import { Option } from "@helpers/option.ts";
import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeAll, describe, expect, it, vi } from "vitest";
import { OptionSelector } from "@/components/table/option.selector.tsx";

const setSelected = vi.fn();

const useIsMobile = vi.fn(() => false);

vi.mock("@/hooks/use.mobile.tsx", () => ({
  useIsMobile: () => useIsMobile(),
}));

beforeAll(() => {
  class ResizeObserverMock {
    observe(): void {}
    unobserve(): void {}
    disconnect(): void {}
  }
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);
  HTMLElement.prototype.hasPointerCapture = () => false;
  HTMLElement.prototype.setPointerCapture = () => {};
  HTMLElement.prototype.releasePointerCapture = () => {};
  HTMLElement.prototype.scrollIntoView = () => {};
});

const options: Option<number>[] = [
  { label: "One", value: 1 },
  { label: "Two", value: 2 },
];

describe("<OptionSelector />", () => {
  afterEach(() => vi.resetAllMocks());

  it("should show placeholder when no option is selected", () => {
    render(<OptionSelector options={options} placeholder="Pick one" setSelected={setSelected} />);

    expect(screen.getByRole("combobox")).toHaveTextContent("Pick one");
  });

  it("should show selected option label when value is set", () => {
    render(
      <OptionSelector
        options={options}
        placeholder="Pick one"
        selected={1}
        setSelected={setSelected}
      />,
    );

    expect(screen.getByRole("combobox")).toHaveTextContent("One");
  });

  it("should call setSelected when user picks an option", async () => {
    render(<OptionSelector options={options} placeholder="Pick one" setSelected={setSelected} />);

    await userEvent.click(screen.getByRole("combobox"));
    await userEvent.click(screen.getByRole("option", { name: "Two" }));

    expect(setSelected).toHaveBeenCalledWith(2);
  });

  it("should open selector on mobile without stealing focus", async () => {
    useIsMobile.mockReturnValue(true);
    render(<OptionSelector options={options} placeholder="Pick one" setSelected={setSelected} />);

    await userEvent.click(screen.getByRole("combobox"));

    expect(screen.getByRole("option", { name: "One" })).toBeInTheDocument();
    useIsMobile.mockReturnValue(false);
  });

  it("should clear selection when user chooses clear action", async () => {
    render(
      <OptionSelector
        options={options}
        placeholder="Pick one"
        selected={1}
        setSelected={setSelected}
      />,
    );

    await userEvent.click(screen.getByRole("combobox"));
    await userEvent.click(screen.getByRole("option", { name: "option.selector.clear" }));

    expect(setSelected).toHaveBeenCalledWith(undefined);
  });
});
