import { RoleStatus } from "@proto/SysRoleProto.ts";

export interface Role {
  id: string;
  name: string;
  code: string;
  status: RoleStatus;
  description?: string | null;
  permissionIds: string[];
  createdAt: number;
}

export type RoleQuery = {
  name?: string;
  status?: RoleStatus | null;
};
