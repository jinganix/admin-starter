import { IPermissionPb, PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { makeAutoObservable } from "mobx";

export class Permission {
  id = "";
  name = "";
  type = PermissionType.API;
  code = "";
  status = PermissionStatus.ACTIVE;
  description?: string;
  createdAt = 0;

  constructor() {
    makeAutoObservable(this);
  }

  static ofPb(pb: IPermissionPb): Permission {
    const v = new Permission();
    v.id = pb.id;
    v.name = pb.name;
    v.type = pb.type;
    v.code = pb.code;
    v.description = pb.description;
    v.status = pb.status;
    v.createdAt = pb.createdAt;
    return v;
  }

  setStatus(status: PermissionStatus): Permission {
    this.status = status;
    return this;
  }
}
