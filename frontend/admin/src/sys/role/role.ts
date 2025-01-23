import { IRolePb, RoleStatus } from "@proto/SysRoleProto.ts";
import { makeAutoObservable } from "mobx";

export class Role {
  id = "";
  name = "";
  code = "";
  status = RoleStatus.ACTIVE;
  description?: string;
  permissionIds: string[] = [];
  createdAt = 0;

  constructor() {
    makeAutoObservable(this);
  }

  static ofPb(pb: IRolePb): Role {
    const v = new Role();
    v.id = pb.id;
    v.name = pb.name;
    v.code = pb.code;
    v.description = pb.description;
    v.status = pb.status;
    v.permissionIds = pb.permissionIds;
    v.createdAt = pb.createdAt;
    return v;
  }

  setStatus(status: RoleStatus): Role {
    this.status = status;
    return this;
  }
}
