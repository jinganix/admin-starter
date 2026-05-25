import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { describe, expect, it } from "vitest";
import { formSchema, valuesResolver } from "@/pages/permission/permission.filter.form.schema.tsx";

describe("permission.filter.form.schema", () => {
  it("should resolve defaults when params are empty", () => {
    expect(valuesResolver.resolve()).toEqual({
      code: "",
      status: null,
      types: [],
    });
  });

  it("should resolve values from search params", () => {
    const params = new URLSearchParams("code=USER_READ&status=0&types=0,1");

    expect(valuesResolver.resolve(params)).toEqual({
      code: "USER_READ",
      status: PermissionStatus.INACTIVE,
      types: [PermissionType.GROUP, PermissionType.API],
    });
  });

  it.each([
    {
      input: { code: "x", status: PermissionStatus.ACTIVE, types: [PermissionType.API] },
      valid: true,
    },
    { input: { code: "", status: null, types: [] }, valid: true },
    { input: { code: "x", status: 99, types: [] }, valid: false },
  ])("should validate filter form when status is $input.status", ({ input, valid }) => {
    expect(formSchema.safeParse(input).success).toBe(valid);
  });
});
