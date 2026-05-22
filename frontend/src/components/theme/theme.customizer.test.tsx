import { render } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { ThemeCustomizer } from "@/components/theme/theme.customizer.tsx";

describe("<ThemeCustomizer />", () => {
  describe("when rendered", () => {
    it("should match snapshot", () => {
      const { container } = render(<ThemeCustomizer />);

      expect(container).toMatchSnapshot();
    });
  });
});
