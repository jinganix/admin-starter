"use client";

import { ChartData } from "@helpers/chart.data.ts";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { CartesianGrid, Line, LineChart, XAxis } from "recharts";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/shadcn/card";
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/shadcn/chart";
import { cn } from "@/helpers/lib/cn";

type Props = {
  chartData: ChartData<object>;
  className?: string;
};

export const OverviewLineChat: FC<Props> = ({ className, chartData }) => {
  const { t } = useTranslation();
  const config = chartData.config(t);

  return (
    <Card className={cn(className)}>
      <CardHeader>
        <CardTitle>{t(chartData.title)}</CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={config}>
          <LineChart accessibilityLayer data={chartData.records} margin={{ left: 12, right: 12 }}>
            <CartesianGrid vertical={false} />
            <XAxis
              dataKey={chartData.xKey}
              tickLine={false}
              axisLine={false}
              tickMargin={8}
              tickFormatter={(value) => value.slice(0, 3)}
            />
            <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />
            {Object.entries(config).map(([key]) => {
              return (
                <Line
                  key={key}
                  dataKey={key}
                  type="natural"
                  stroke={`var(--color-${key})`}
                  strokeWidth={2}
                  dot={{
                    fill: `var(--color-${key})`,
                  }}
                  activeDot={{
                    r: 6,
                  }}
                />
              );
            })}
          </LineChart>
        </ChartContainer>
      </CardContent>
    </Card>
  );
};
