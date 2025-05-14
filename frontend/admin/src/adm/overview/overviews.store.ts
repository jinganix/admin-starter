import { HttpService } from "@helpers/network/http.service.ts";
import { IOverviewPb, OverviewListRequest, OverviewListResponse } from "@proto/AdmOverviewProto.ts";
import dayjs from "dayjs";
import i18next from "i18next";
import { gt, sum } from "lodash";
import { makeAutoObservable } from "mobx";
import { container } from "tsyringe";
import { Overview } from "@/adm/overview/overview.ts";
import { ChartData, ChartConfigProvider } from "@/helpers/chart.data";

export interface ApiChartItem {
  month: string;
  get: number;
  post: number;
}

export interface EntityChartItem {
  month: string;
  created: number;
  deleted: number;
}

const entityConfig: ChartConfigProvider = (t) => ({
  created: {
    color: "var(--chart-2)",
    label: t("overview.chart.label.created"),
  },
  deleted: {
    color: "var(--chart-1)",
    label: t("overview.chart.label.deleted"),
  },
});

export class OverviewsStore {
  records: Overview[] = [];
  apiGet = 0;
  apiPost = 0;
  userCreated = 0;
  userDeleted = 0;
  roleCreated = 0;
  roleDeleted = 0;
  permissionCreated = 0;
  permissionDeleted = 0;
  roleData: ChartData<EntityChartItem> = new ChartData(
    "overview.chart.role",
    "month",
    entityConfig,
  );
  userData: ChartData<EntityChartItem> = new ChartData(
    "overview.chart.user",
    "month",
    entityConfig,
  );
  permissionData: ChartData<EntityChartItem> = new ChartData(
    "overview.chart.permission",
    "month",
    entityConfig,
  );
  apiData: ChartData<ApiChartItem> = new ChartData("overview.chart.apiCalled", "month", (t) => ({
    get: {
      color: "var(--chart-2)",
      label: t("overview.chart.label.get"),
    },
    post: {
      color: "var(--chart-1)",
      label: t("overview.chart.label.post"),
    },
  }));

  constructor() {
    makeAutoObservable(this);
  }

  async load(): Promise<void> {
    const res = await container
      .resolve(HttpService)
      .request(OverviewListRequest.create(), OverviewListResponse);
    res && this.calculate(res.records);
  }

  private calculate(pbs: IOverviewPb[]): void {
    const compare = (a: Overview, b: Overview): number => (gt(a.month, b.month) ? 1 : -1);
    const records = pbs.map((x) => Overview.ofPb(x)).sort(compare);
    this.records = records;
    this.apiGet = sum(records.map((x) => x.apiGet));
    this.apiPost = sum(records.map((x) => x.apiPost));
    this.userCreated = sum(records.map((x) => x.userCreated));
    this.userDeleted = sum(records.map((x) => x.userDeleted));
    this.roleCreated = sum(records.map((x) => x.roleCreated));
    this.roleDeleted = sum(records.map((x) => x.roleDeleted));
    this.permissionCreated = sum(records.map((x) => x.permissionCreated));
    this.permissionDeleted = sum(records.map((x) => x.permissionDeleted));
    this.apiData.records = records
      .sort()
      .map((x) => ({
        get: x.apiGet,
        month: i18next.t(`month.abbr.${dayjs(x.month).month() + 1}`),
        post: x.apiPost,
      }))
      .slice(-12);
    this.roleData.records = records
      .map((x) => ({
        created: x.roleCreated,
        deleted: x.roleDeleted,
        month: i18next.t(`month.abbr.${dayjs(x.month).month() + 1}`),
      }))
      .slice(-12);
    this.userData.records = records
      .map((x) => ({
        created: x.userCreated,
        deleted: x.userDeleted,
        month: i18next.t(`month.abbr.${dayjs(x.month).month() + 1}`),
      }))
      .slice(-12);
    this.permissionData.records = records
      .map((x) => ({
        created: x.permissionCreated,
        deleted: x.permissionDeleted,
        month: i18next.t(`month.abbr.${dayjs(x.month).month() + 1}`),
      }))
      .slice(-12);
  }
}

export const overviewsStore = new OverviewsStore();
