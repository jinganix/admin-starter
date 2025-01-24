import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";

export interface Permission {
  id: string;
  name: string;
  type: PermissionType;
  code: string;
  status: PermissionStatus;
  description?: string;
  createdAt: number;
}

export type PermissionQuery = {
  code?: string;
  status?: PermissionStatus;
  types?: PermissionType[];
};

export interface PermissionOption {
  label: string;
  value: string;
  code: string;
}
