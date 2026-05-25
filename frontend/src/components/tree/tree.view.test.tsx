import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TreeView } from "@/components/tree/tree.view.tsx";
import { TreeStateProvider } from "@/components/tree/use.tree.state.tsx";

describe("<TreeView />", () => {
  const setSelected = vi.fn();
  const items = [
    {
      code: "U",
      items: [{ code: "R", label: "Read", value: "read" }],
      label: "Users",
      value: "users",
    },
  ];

  it("should render tree items when mounted", () => {
    render(
      <TreeStateProvider>
        <TreeView
          title="Search"
          className="test-tree"
          items={items}
          selected={[]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    expect(screen.getByPlaceholderText("Search")).toBeInTheDocument();
    expect(screen.getByText("Users")).toBeInTheDocument();
  });

  it("should filter items when user types keyword", async () => {
    render(
      <TreeStateProvider state="expanded">
        <TreeView
          title="Search"
          className="test-tree"
          items={items}
          selected={[]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    await userEvent.type(screen.getByPlaceholderText("Search"), "read");

    expect(screen.getByText("Read")).toBeInTheDocument();
    expect(screen.getByText("Users")).toBeInTheDocument();
  });

  it("should clear keyword when user clicks clear button", async () => {
    render(
      <TreeStateProvider>
        <TreeView
          title="Search"
          className="test-tree"
          items={items}
          selected={[]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    await userEvent.type(screen.getByPlaceholderText("Search"), "read");
    await userEvent.click(screen.getAllByRole("button")[0]);

    expect(screen.getByPlaceholderText("Search")).toHaveValue("");
    expect(screen.getByText("Users")).toBeInTheDocument();
  });
});
