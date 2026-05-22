import { HttpService } from "@helpers/network/http.service.ts";
import { pbToPaging } from "@helpers/paging/pageable.ts";
import { DataLoader } from "@helpers/table/table.types.ts";
import { AuditListRequest, AuditListResponse } from "@proto/SysAuditProto.ts";
import { container } from "tsyringe";
import { Audit, AuditQuery } from "@/sys/audit/audit.types.ts";

export class AuditActions {
  static list: DataLoader<AuditQuery, Audit> = async (pageable, query) => {
    const res = await container
      .resolve(HttpService)
      .request(AuditListRequest.create({ pageable, ...query }), AuditListResponse);
    if (res) {
      return { paging: pbToPaging(res), records: res.records };
    }
    return null;
  };
}
