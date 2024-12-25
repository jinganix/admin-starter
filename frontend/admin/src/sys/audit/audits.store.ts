import { HttpService } from "@helpers/network/http.service.ts";
import { Pageable } from "@helpers/paging/pageable.ts";
import { Paging } from "@helpers/paging/paging.ts";
import { AuditListRequest, AuditListResponse, IAuditListRequest } from "@proto/SysAuditProto.ts";
import { isEqual } from "lodash";
import { makeAutoObservable, runInAction } from "mobx";
import { container } from "tsyringe";
import { Audit } from "@/sys/audit/audit.ts";

export class AuditsStore {
  private query?: IAuditListRequest;
  private loadedAt?: number;
  paging = new Paging();
  records: Audit[] = [];

  constructor() {
    makeAutoObservable(this);
  }

  private checkSkip(query?: IAuditListRequest): boolean {
    if (isEqual(this.query, query) && (this.loadedAt ?? 0) + 100 > Date.now()) {
      return true;
    }
    this.query = query;
    this.loadedAt = Date.now();
    return false;
  }

  async load(pageable: Pageable, username?: string, method?: string, path?: string): Promise<void> {
    if (!this.checkSkip({ method, pageable, path, username })) {
      await this.reload();
    }
  }

  async reload(): Promise<void> {
    const res = await container
      .resolve(HttpService)
      .request(AuditListRequest.create(this.query), AuditListResponse);
    if (res) {
      runInAction(() => {
        this.paging = Paging.ofPb(res);
        this.records = res.records.map((x) => Audit.ofPb(x));
      });
    }
  }
}

export const auditsStore = new AuditsStore();
