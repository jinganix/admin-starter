import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { TableTitle } from "@/components/layout/table.title.tsx";

describe("<TableTitle />", () => {
  it("should render title and subtitle when mounted", () => {
    render(<TableTitle title="Users" sub="Manage users" />);

    expect(screen.getByRole("heading", { name: "Users" })).toBeInTheDocument();
    expect(screen.getByText("Manage users")).toBeInTheDocument();
  });
});
