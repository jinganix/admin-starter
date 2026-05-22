"use client";

import { ChartData } from "@helpers/chart.data.ts";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Bar, BarChart, CartesianGrid, XAxis } from "recharts";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/shadcn/card";
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/shadcn/chart";
import { cn } from "@/helpers/lib/cn";

type Props = {
  chartData: ChartData<object>;
  className?: string;
};

export const OverviewBarChart: FC<Props> = ({ className, chartData }) => {
  const { t } = useTranslation();
  const config = chartData.config(t);

  return (
    <Card className={cn(className)}>
      <CardHeader>
        <CardTitle>{t(chartData.title)}</CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={config}>
          <BarChart accessibilityLayer data={chartData.records}>
            <CartesianGrid vertical={false} />
            <XAxis
              dataKey={chartData.xKey}
              tickLine={false}
              tickMargin={10}
              axisLine={false}
              tickFormatter={(value) => value.slice(0, 3)}
            />
            <ChartTooltip cursor={false} content={<ChartTooltipContent indicator="dashed" />} />
            {Object.entries(config).map(([key]) => {
              return <Bar key={key} dataKey={key} fill={`var(--color-${key})`} radius={4} />;
            })}
          </BarChart>
        </ChartContainer>
      </CardContent>
    </Card>
  );
};
