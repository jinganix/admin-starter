import { RoleStatus } from "@proto/SysRoleProto.ts";
import { describe, expect, it } from "vitest";
import { formSchema, valuesResolver } from "@/pages/role/role.filter.form.schema.tsx";

describe("role.filter.form.schema", () => {
  it("should resolve defaults when params are empty", () => {
    expect(valuesResolver.resolve()).toEqual({
      name: "",
      status: null,
    });
  });

  it("should resolve values from search params", () => {
    const params = new URLSearchParams("name=admin&status=0");

    expect(valuesResolver.resolve(params)).toEqual({
      name: "admin",
      status: RoleStatus.INACTIVE,
    });
  });

  it.each([
    { input: { name: "admin", status: RoleStatus.ACTIVE }, valid: true },
    { input: { name: "", status: null }, valid: true },
    { input: { name: "admin", status: 99 }, valid: false },
  ])("should validate filter form when status is $input.status", ({ input, valid }) => {
    expect(formSchema.safeParse(input).success).toBe(valid);
  });
});
