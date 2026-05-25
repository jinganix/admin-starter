import { emitter } from "@helpers/event/emitter.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { render, screen } from "@testing-library/react";
import { toast } from "sonner";
import { afterEach, describe, expect, it, vi } from "vitest";
import { Toaster } from "@/components/ui/toaster.tsx";
import { ConfigProvider } from "@/hooks/use.config.tsx";

vi.mock("sonner", () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

vi.mock("@/components/shadcn/sonner.tsx", () => ({
  Toaster: ({ theme }: { theme: string }) => <div data-testid="sonner" data-theme={theme} />,
}));

describe("<Toaster />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render sonner with config theme when mounted", () => {
    render(
      <ConfigProvider defaultConfig={{ mode: "dark", radius: 0.5, theme: "zinc" }}>
        <Toaster />
      </ConfigProvider>,
    );

    expect(screen.getByTestId("sonner")).toHaveAttribute("data-theme", "dark");
  });

  it("should show success toast when ok error is emitted", () => {
    render(
      <ConfigProvider>
        <Toaster />
      </ConfigProvider>,
    );

    emitter.emit("error", ErrorCode.OK);

    expect(toast.success).toHaveBeenCalledWith(
      expect.stringContaining(`error.code.${ErrorCode.OK}_OK`),
    );
  });

  it("should show status toast when generic error has status", () => {
    render(
      <ConfigProvider>
        <Toaster />
      </ConfigProvider>,
    );

    emitter.emit("error", ErrorCode.ERROR, 500);

    expect(toast.error).toHaveBeenCalledWith("error.status.500");
  });

  it("should show code toast when error code is emitted", () => {
    render(
      <ConfigProvider>
        <Toaster />
      </ConfigProvider>,
    );

    emitter.emit("error", ErrorCode.BAD_TOKEN);

    expect(toast.error).toHaveBeenCalledWith(
      expect.stringContaining(`error.code.${ErrorCode.BAD_TOKEN}_BAD_TOKEN`),
    );
  });
});
