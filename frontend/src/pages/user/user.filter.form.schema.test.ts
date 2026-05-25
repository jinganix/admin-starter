import { UserStatus } from "@proto/SysUserProto.ts";
import { describe, expect, it } from "vitest";
import { formSchema, valuesResolver } from "@/pages/user/user.filter.form.schema.tsx";

describe("user.filter.form.schema", () => {
  it("should resolve defaults when params are empty", () => {
    expect(valuesResolver.resolve()).toEqual({
      status: null,
      userId: "",
      username: "",
    });
  });

  it("should resolve values from search params", () => {
    const params = new URLSearchParams("userId=42&username=alice&status=1");

    expect(valuesResolver.resolve(params)).toEqual({
      status: UserStatus.ACTIVE,
      userId: "42",
      username: "alice",
    });
  });

  it.each([
    { input: { status: UserStatus.ACTIVE, userId: "1", username: "bob" }, valid: true },
    { input: { status: null, userId: "", username: "" }, valid: true },
    { input: { status: 99, userId: "1", username: "bob" }, valid: false },
  ])("should validate filter form when status is $input.status", ({ input, valid }) => {
    const result = formSchema.safeParse(input);
    expect(result.success).toBe(valid);
  });
});
