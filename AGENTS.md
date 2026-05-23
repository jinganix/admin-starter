# admin-starter — Agent Guide

## Read first

- [ai-kit/JAVA_TEST_CONVENTIONS.md](ai-kit/JAVA_TEST_CONVENTIONS.md) (required before editing `service/src/test/**`)
- [ai-kit/PROJECT_STRUCTURE.md](ai-kit/PROJECT_STRUCTURE.md)
- [.cursor/rules/java-tests.mdc](.cursor/rules/java-tests.mdc)

## Commands

Run all `./gradlew` commands from repository root:

```bash
./gradlew :service:test
./gradlew :service:build
```

## Required Java test rules (short)

- Test only classes with logic (`*Handler`, branching `*Service`, controllers with validation/auth).
- Integration tests must extend `tests.SpringBootIntegrationTests` and call `testHelper.clearAll()` in `@BeforeEach`.
- `@MockitoSpyBean` only in `SpringBootIntegrationTests` (never in subclasses).
- Single-entry handler (`handle` only): flat outer `@Test`, no `@Nested`, method names `givenXxx`.
- Handler tests cover business logic only; request validation belongs to controller tests.
- Controller tests: one `@Nested` per controller method, flat tests inside.
- Request validation: parameterized with request objects (`@MethodSource`), boundary coverage required.
- Put authorization checks after validation cases.
- Keep one success case per controller method; extra business branches go to handler tests.

## Backend layering (short)

- `sys/*` = platform (auth, RBAC, audit); `adm/*` = business features; `helper/*` = infra; `setup/*` = Spring wiring.
- Per API: `Controller` → `Handler` → optional `Service` → `Repository`; models are persistence records.
- Full conventions: [ai-kit/PROJECT_STRUCTURE.md](ai-kit/PROJECT_STRUCTURE.md).

## Key paths

- Backend source: `service/src/main/java/io/github/jinganix/admin/starter/`
- Backend tests: `service/src/test/java/io/github/jinganix/admin/starter/`
- Reference controller test: `service/src/test/java/io/github/jinganix/admin/starter/sys/auth/AuthControllerTest.java`
