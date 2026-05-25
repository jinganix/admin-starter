import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MemoryRouter } from "react-router";
import { describe, expect, it, vi } from "vitest";
import { Error403 } from "@/pages/error/403.tsx";

const navigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigate,
  };
});

vi.mock("@/sys/user/user.utils.ts", () => ({
  logout: vi.fn(),
}));

describe("<Error403 />", () => {
  it("should render forbidden message and actions", () => {
    render(
      <MemoryRouter>
        <Error403 />
      </MemoryRouter>,
    );

    expect(screen.getByText("403")).toBeInTheDocument();
    expect(screen.getByText("error.page.403.reason")).toBeInTheDocument();
  });

  it("should navigate home when home button is clicked", async () => {
    render(
      <MemoryRouter>
        <Error403 />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByText("error.page.home"));
    expect(navigate).toHaveBeenCalledWith("/");
  });

  it("should navigate back when go back button is clicked", async () => {
    navigate.mockClear();
    render(
      <MemoryRouter>
        <Error403 />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByText("error.page.goBack"));
    expect(navigate).toHaveBeenCalledWith(-1);
  });

  it("should sign out when sign out button is clicked", async () => {
    const { logout } = await import("@/sys/user/user.utils.ts");
    render(
      <MemoryRouter>
        <Error403 />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByText("error.page.signOut"));
    expect(logout).toHaveBeenCalledOnce();
  });
});
