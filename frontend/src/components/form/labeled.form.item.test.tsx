import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { LabeledFormItem } from "@/components/form/labeled.form.item.tsx";

vi.mock("@/components/shadcn/form.tsx", () => ({
  FormControl: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  FormItem: ({ children, className }: { children: React.ReactNode; className?: string }) => (
    <div className={className}>{children}</div>
  ),
  FormLabel: ({ children, className }: { children: React.ReactNode; className?: string }) => (
    <label className={className}>{children}</label>
  ),
  FormMessage: () => <span>form-message</span>,
}));

describe("<LabeledFormItem />", () => {
  it("should render label and controlled input when mounted", () => {
    render(
      <LabeledFormItem label="Name">
        <input aria-label="name-input" />
      </LabeledFormItem>,
    );

    expect(screen.getByText("Name")).toBeInTheDocument();
    expect(screen.getByLabelText("name-input")).toBeInTheDocument();
    expect(screen.getByText("form-message")).toBeInTheDocument();
  });

  it("should render uncontrolled children when controlled is false", () => {
    render(
      <LabeledFormItem label="Name" controlled={false}>
        <input aria-label="name-input" />
      </LabeledFormItem>,
    );

    expect(screen.getByLabelText("name-input")).toBeInTheDocument();
  });
});
