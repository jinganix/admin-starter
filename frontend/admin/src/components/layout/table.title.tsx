import { FC } from "react";

type Props = {
  title: string;
  sub: string;
};

export const TableTitle: FC<Props> = ({ title, sub }) => {
  return (
    <div className="flex items-center justify-between space-y-2 flex-wrap">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">{title}</h2>
        <p className="text-muted-foreground">{sub}</p>
      </div>
    </div>
  );
};
