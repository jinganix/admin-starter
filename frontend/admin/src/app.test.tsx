import { render, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { App } from "@/app.tsx";
import * as useLoading from "@/hooks/use.loading.ts";
import * as routes from "@/routes.tsx";

describe("<App />", () => {
  beforeEach(() => vi.spyOn(routes, "getRoutes").mockResolvedValue([]));

  describe("when loading", () => {
    it("should render spinner", () => {
      const spyUserLoading = vi.spyOn(useLoading, "useLoading").mockReturnValue([true, vi.fn()]);
      const element = render(<App />);

      waitFor(() => expect(element.queryByTestId("spinner")).toBeInTheDocument());
      expect(spyUserLoading).toHaveBeenCalledTimes(1);
    });
  });

  describe("when not loading", () => {
    it("should render routes", () => {
      const spyUserLoading = vi.spyOn(useLoading, "useLoading").mockReturnValue([false, vi.fn()]);
      const element = render(<App />);

      waitFor(() => expect(element.queryByTestId("browser-router")).toBeInTheDocument());
      expect(spyUserLoading).toHaveBeenCalledTimes(1);
    });
  });
});
