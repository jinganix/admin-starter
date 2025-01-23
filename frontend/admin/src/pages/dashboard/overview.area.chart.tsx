"use client";

import { ChartData } from "@helpers/chart.data.ts";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Area, AreaChart, CartesianGrid, XAxis } from "recharts";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/shadcn/card";
import {
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/shadcn/chart";
import { cn } from "@/helpers/lib/cn";

type Props = {
  chartData: ChartData<object>;
  className?: string;
};

export const OverviewAreaChart: FC<Props> = ({ className, chartData }) => {
  const { t } = useTranslation();
  const config = chartData.config(t);

  return (
    <Card className={cn(className)}>
      <CardHeader>
        <CardTitle>{t(chartData.title)}</CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={config}>
          <AreaChart accessibilityLayer data={chartData.records} margin={{ left: 12, right: 12 }}>
            <CartesianGrid vertical={false} />
            <XAxis
              dataKey={chartData.xKey}
              tickLine={false}
              axisLine={false}
              tickMargin={8}
              tickFormatter={(value) => value.slice(0, 3)}
            />
            <ChartTooltip cursor={false} content={<ChartTooltipContent indicator="line" />} />
            {Object.entries(config).map(([key]) => {
              return (
                <Area
                  key={key}
                  dataKey={key}
                  type="natural"
                  fill={`var(--color-${key})`}
                  fillOpacity={0.4}
                  stroke={`var(--color-${key})`}
                  stackId="a"
                />
              );
            })}
            <ChartLegend content={<ChartLegendContent />} />
          </AreaChart>
        </ChartContainer>
      </CardContent>
    </Card>
  );
};
