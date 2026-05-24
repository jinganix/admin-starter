import { Trans } from "@helpers/i18n";
import { ChartConfig } from "@/components/shadcn/chart.tsx";

export type ChartConfigProvider = (t: Trans) => ChartConfig;

export class ChartData<T> {
  title: string;
  xKey: string;
  config: ChartConfigProvider;
  records: T[];

  constructor(title: string, xKey: string, config: ChartConfigProvider, records: T[] = []) {
    this.title = title;
    this.xKey = xKey;
    this.config = config;
    this.records = records;
  }
}
