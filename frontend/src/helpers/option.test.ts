import i18next from "i18next";
import { describe, expect, it, vi } from "vitest";
import { enumToOptions } from "@/helpers/option.ts";

describe("enumToOptions", () => {
  it("should map string enum values to labeled options", () => {
    vi.spyOn(i18next, "t").mockImplementation(((key: string) => `t:${key}`) as typeof i18next.t);

    enum Sample {
      A = 1,
      B = 2,
    }

    expect(enumToOptions(Sample, "sample")).toEqual([
      { label: "t:sample.A", value: 1 },
      { label: "t:sample.B", value: 2 },
    ]);
  });
});
