# admin-starter — Agent Guide

## Read first

- [ai-kit/BACKEND_TEST_CONVENTIONS.md](ai-kit/BACKEND_TEST_CONVENTIONS.md) (required before editing `service/src/test/**`)
- [ai-kit/FRONTEND_TEST_CONVENTIONS.md](ai-kit/FRONTEND_TEST_CONVENTIONS.md) (required before editing `frontend/src/**/*.test.{ts,tsx}`)
- [ai-kit/PROJECT_STRUCTURE.md](ai-kit/PROJECT_STRUCTURE.md)
- [.cursor/rules/java-tests.mdc](.cursor/rules/java-tests.mdc)
- [.cursor/rules/frontend-tests.mdc](.cursor/rules/frontend-tests.mdc)

## Commands

Run all `./gradlew` commands from repository root:

```bash
./gradlew :service:test
./gradlew :service:build
```

## Required Java test rules (short)

- Behavior-first: organize by outcome/condition, not by mirroring source method names; `@Nested` only when setup is shared (at most one level).
- Test only classes with logic (`*Handler`, branching `*Service`, controllers with validation/auth).
- Integration tests must extend `tests.SpringBootIntegrationTests` and call `testHelper.clearAll()` in `@BeforeEach`.
- `@MockitoSpyBean` only in `SpringBootIntegrationTests` (never in subclasses).
- Single-entry handler (`handle` only): flat outer `@Test`, no `@Nested`, method names `shouldReturnXxxWhenYyy`.
- Handler tests cover business logic only; request validation belongs to API boundary (controller) tests.
- Service tests: cover branching via observable behavior; flat by default.
- API boundary tests: validation parameterized with `InvalidRequestCase` (error + request + description), boundary coverage required; authorization after validation.
- One happy-path success per behavior; extra business branches go to handler/service tests.

## Backend layering (short)

- `sys/*` = platform (auth, RBAC, audit); `adm/*` = business features; `helper/*` = infra; `setup/*` = Spring wiring.
- Per API: `Controller` → `Handler` → optional `Service` → `Repository`; models are persistence records.
- Full conventions: [ai-kit/PROJECT_STRUCTURE.md](ai-kit/PROJECT_STRUCTURE.md).

## Key paths

- Backend source: `service/src/main/java/io/github/jinganix/admin/starter/`
- Backend tests: `service/src/test/java/io/github/jinganix/admin/starter/`
- Reference controller test: `service/src/test/java/io/github/jinganix/admin/starter/sys/auth/AuthControllerTest.java`
