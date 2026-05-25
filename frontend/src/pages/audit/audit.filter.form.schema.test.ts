import { describe, expect, it } from "vitest";
import { formSchema, valuesResolver } from "@/pages/audit/audit.filter.form.schema.tsx";

describe("audit.filter.form.schema", () => {
  it("should resolve defaults when params are empty", () => {
    expect(valuesResolver.resolve()).toEqual({
      method: "",
      path: "",
      userId: "",
      username: "",
    });
  });

  it("should resolve values from search params", () => {
    const params = new URLSearchParams("userId=1&username=alice&path=/api&method=GET");

    expect(valuesResolver.resolve(params)).toEqual({
      method: "GET",
      path: "/api",
      userId: "1",
      username: "alice",
    });
  });

  it("should accept optional filter fields", () => {
    expect(
      formSchema.safeParse({ method: "POST", path: "/x", userId: "2", username: "bob" }).success,
    ).toBe(true);
  });
});
