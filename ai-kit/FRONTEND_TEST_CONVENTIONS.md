# Frontend testing conventions

> Read this document before editing tests under `frontend/src/**/*.test.tsx`.
> Run tests from `frontend/`: `npm run test`

---

## 1. Scope

- Test components, pages, hooks, and helpers with real behavior branches.
- Skip snapshot-only coverage for pure pass-through wrappers unless they protect key layout contracts.
- Prefer behavior assertions over implementation detail assertions.

---

## 2. Behavior-first organization

Applies to **all** frontend tests:

- Organize around **user-observable behavior** (what the user sees or what the app exposes), not around source method names, file exports, or route handler names (`foo`, `bar`, `create`, `submit`).
- Keep one outer `describe` for the unit under test (`<Component />`, hook name, or module).
- Prefer flat `it` cases on that outer `describe`. Add one nested `describe` only when several cases share the same Arrange setup or scope one behavior slice.
- At most one nested `describe` level. Avoid `describe` inside `describe` inside `describe`.
- Do **not** require one nested group per function, prop callback, or page action.
- Prefer concise BDD sentence format:
  - `it`: `should ... when ...` (preferred)
  - `describe`: `when ...` (only when grouping is needed)
- Use lowercase text in description strings (do not start with uppercase).

---

## 3. Layout and naming

- Put tests next to source files when possible: `{name}.test.tsx` / `{name}.test.ts`.

---

## 4. Component tests

For presentational or interaction components:

- Group by user-observable state or interaction only when grouping reduces duplication.
- Cover at least:
  - one success/expected path
  - one failure/edge path (empty state, disabled state, rejected callback, etc.)
- Mock child components or heavy integrations when the component contract is the target.
- Avoid asserting private React internals, state variables, or hook call order unless it is the behavior contract.

Example:

```tsx
describe("<DeleteButton />", () => {
  it("should open dialog when user clicks delete", async () => {
    // Arrange
    // Act
    // Assert
  });
});
```

---

## 5. Page and form tests

- Organize by **flow or outcome** the user cares about (for example `when form is submitted with invalid email`), not by mirroring every handler or action name in the page file.
- Keep tests flat by default; use `describe("when ...")` only when multiple cases share setup.
- Validate request/input errors before permission/auth or side-effect checks.
- Keep one happy-path success case per behavior under test; move extra branch combinations to lower-level component/hook tests.

### 5.1 Validation coverage

- Prefer parameterized tests (`it.each`) for invalid form/request cases.
- Put invalid case data near the related test.
- Include representative boundary cases per constrained field:
  - `undefined` / empty
  - below minimum
  - above maximum
  - pattern mismatch (if present)

Each invalid case should include expected message (or behavior), input, and short description.

---

## 6. BDD wording format

- Keep wording business-facing and behavior-focused; avoid implementation terms (`setState`, `useEffect called`, etc.).
- Frontend tests use one of these formats:
  - preferred: `it("should <outcome> when <condition>")`
  - grouped: `describe("when <condition>")` + `it("should <outcome>")`
- Keep wording short and specific, for example: `should return BAD_TOKEN when token is malformed`.
- Keep the first word lowercase in description strings.

---

## 7. Hooks and helper tests

- Hook tests focus on returned contract and externally visible side effects.
- Helper tests should be deterministic and table-driven (`it.each`) when possible.
- Time, random, and network-dependent paths must be mocked (`vi.useFakeTimers`, `vi.spyOn`, `vi.mock`).
- Multiple exported functions in one module: test each **behavior** with flat `it` cases; nest only when setup is shared, not because the file exports `foo` and `bar`.

---

## 8. Test setup and assertions

- Shared setup is in `frontend/tests/setup.ts`; do not duplicate global cleanup logic.
- Use Testing Library queries by role/label/text as first choice.
- Async UI checks should use `findBy...` or `waitFor`.
- Keep snapshots focused; avoid large snapshots that hide behavior regressions.
- Reset mocks in `afterEach` when tests mutate shared mocks.

---

## 9. Commands

```bash
cd frontend
npm run test
npm run coverage
npm run test -- src/components/ui/delete.button.test.tsx
```

---

## 10. Quick checklist

- Behavior-first: structure and names reflect outcomes/conditions, not a 1:1 map to source methods or actions.
- Test real behavior branches; skip branchless pass-through code.
- Flat by default; at most one nested `describe` when setup is shared.
- Prefer concise BDD descriptions: `should ... when ...`.
- Validation cases are parameterized and include boundary coverage.
- Validation checks come before permission/side-effect checks.
- One happy-path success per behavior; extra branches go to lower-level tests.
