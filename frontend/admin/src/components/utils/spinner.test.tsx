import { render } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { Spinner } from "@/components/utils/spinner.tsx";

describe("<Spinner />", () => {
  describe("when loading is true", () => {
    it("should match snapshot", () => {
      const { container } = render(<Spinner loading />);

      expect(container).toMatchSnapshot();
    });
  });

  describe("when loading is false", () => {
    it("should be empty", () => {
      const { container } = render(<Spinner loading={false} />);

      expect(container).toBeEmptyDOMElement();
    });
  });
});
