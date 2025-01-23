import { motion } from "framer-motion";
import { ArrowRightLeftIcon, LockIcon, UserRoundCheckIcon, UsersIcon } from "lucide-react";
import { observer } from "mobx-react-lite";
import { FC, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { overviewsStore } from "@/adm/overview/overviews.store.ts";
import { LayoutContent } from "@/components/layout/layout.content.tsx";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/shadcn/card";
import { OverviewAreaChart } from "@/pages/dashboard/overview.area.chart.tsx";
import { OverviewBarChart } from "@/pages/dashboard/overview.bar.chart.tsx";
import { OverviewLineChat } from "@/pages/dashboard/overview.line.chart.tsx";

const DashboardComponent: FC = observer(() => {
  const { t } = useTranslation();

  useEffect(() => void overviewsStore.load(), []);

  const cards = [
    {
      icon: <ArrowRightLeftIcon />,
      title: t("overview.apiCalled"),
      value: overviewsStore.apiGet + overviewsStore.apiPost,
    },
    {
      icon: <UsersIcon />,
      title: t("overview.userCreated"),
      value: overviewsStore.userCreated,
    },
    {
      icon: <UserRoundCheckIcon />,
      title: t("overview.roleCreated"),
      value: overviewsStore.roleCreated,
    },
    {
      icon: <LockIcon />,
      title: t("overview.permissionCreated"),
      value: overviewsStore.permissionCreated,
    },
  ];

  return (
    <div className="flex-col md:flex flex-1 p-4 md:px-8 space-y-4 xl:space-y-6">
      <div className="grid gap-4 xl:gap-6 md:grid-cols-2 lg:grid-cols-4">
        {cards.map((x) => (
          <motion.div key={x.title} whileHover={{ scale: 1.05, transition: { duration: 0.2 } }}>
            <Card className="h-full">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{x.title}</CardTitle>
                {x.icon}
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{x.value}</div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>
      <div className="grid gap-4 xl:gap-6 md:grid-cols-2 lg:grid-cols-4">
        {[
          <OverviewAreaChart chartData={overviewsStore.apiData} />,
          <OverviewBarChart chartData={overviewsStore.userData} />,
          <OverviewLineChat chartData={overviewsStore.roleData} />,
          <OverviewLineChat chartData={overviewsStore.permissionData} />,
        ].map((x, index) => (
          <motion.div
            key={index}
            className="col-span-2"
            whileHover={{ scale: 1.02, transition: { duration: 0.2 } }}
          >
            {x}
          </motion.div>
        ))}
      </div>
    </div>
  );
});

export const DashboardPage: FC = () => {
  return (
    <LayoutContent>
      <DashboardComponent />
    </LayoutContent>
  );
};
