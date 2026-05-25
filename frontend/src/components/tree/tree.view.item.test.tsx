import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TreeViewItem } from "@/components/tree/tree.view.item.tsx";
import { TreeStateProvider } from "@/components/tree/use.tree.state.tsx";

describe("<TreeViewItem />", () => {
  const setSelected = vi.fn();

  it("should render leaf checkbox when item has no children", () => {
    render(
      <TreeStateProvider>
        <TreeViewItem
          item={{ code: "R", label: "Read", value: "read" }}
          selected={[]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    expect(screen.getByText("Read")).toBeInTheDocument();
    expect(screen.getByRole("checkbox")).toBeInTheDocument();
  });

  it("should select leaf when user checks checkbox", async () => {
    render(
      <TreeStateProvider>
        <TreeViewItem
          item={{ code: "R", label: "Read", value: "read" }}
          selected={[]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    await userEvent.click(screen.getByRole("checkbox"));

    expect(setSelected).toHaveBeenCalledWith(["read"]);
  });

  it("should render collapsible group when item has children", async () => {
    render(
      <TreeStateProvider>
        <TreeViewItem
          item={{
            code: "U",
            items: [{ code: "R", label: "Read", value: "read" }],
            label: "Users",
            value: "users",
          }}
          selected={["read"]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    expect(screen.getByText("Users")).toBeInTheDocument();
    await userEvent.click(screen.getByRole("button", { name: /Users/i }));
    expect(screen.getByText("Read")).toBeInTheDocument();
  });

  it("should clear selection when user unchecks leaf", async () => {
    render(
      <TreeStateProvider>
        <TreeViewItem
          item={{ code: "R", label: "Read", value: "read" }}
          selected={["read"]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    await userEvent.click(screen.getByRole("checkbox"));

    expect(setSelected).toHaveBeenCalledWith(undefined);
  });

  it("should select all descendants when parent is checked", async () => {
    render(
      <TreeStateProvider state="expanded">
        <TreeViewItem
          item={{
            code: "U",
            items: [{ code: "R", label: "Read", value: "read" }],
            label: "Users",
            value: "users",
          }}
          selected={[]}
          setSelected={setSelected}
        />
      </TreeStateProvider>,
    );

    await userEvent.click(screen.getAllByRole("checkbox")[0]);

    expect(setSelected).toHaveBeenCalledWith(["users", "read"]);
  });
});
