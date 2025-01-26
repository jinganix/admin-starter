import { RoleStatus } from "@proto/SysRoleProto.ts";

export interface Role {
  id: string;
  name: string;
  code: string;
  status: RoleStatus;
  description?: string;
  permissionIds: string[];
  createdAt: number;
}

export type RoleQuery = {
  name?: string;
  status?: RoleStatus | null;
};
