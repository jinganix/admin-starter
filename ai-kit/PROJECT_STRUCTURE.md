# Project Structure

## Core directories

- `service/`: Spring Boot backend.
- `frontend/`: React + Vite frontend.
- `proto/admin/`: business proto definitions.
- `proto/imports/`: shared proto options/imports.
- `ai-kit/`: AI-focused conventions and agent docs.

## Backend (`service/`)

- `src/main/java/io/github/jinganix/admin/starter/`: backend source root.
- `src/test/java/io/github/jinganix/admin/starter/`: backend test root.
- `src/main/resources/db/migration/`: Flyway migrations.

---

## Top-level packages

| Package | Role | Extend when |
|---------|------|-------------|
| `sys/*` | **Platform**: auth, users, RBAC, audit. Shared across all products forked from this starter. | Fixing or extending admin capabilities (login, roles, permissions). |
| `adm/*` | **Business**: product-specific features. Each subpackage is one bounded feature (e.g. `adm/overview`). | Adding new business APIs and tables. |
| `helper/*` | **Infrastructure**: JWT, JOOQ, Redis, exceptions, UID, etc. No product rules. | Reusable technical utilities only. |
| `setup/*` | **Composition**: Spring config, global exception handling, argument resolvers. | Wiring and cross-cutting framework setup. |

**Dependency rule (target):** `adm` → `sys` / `helper` is allowed; `sys` must not depend on `adm`.  
Today `sys` still references `adm/overview` in `DataInitializer` and `RequestAuditAspect` for demo data and metrics—treat that as starter demo coupling; remove or invert when forking for production.

---

## Feature module layout

Each feature under `sys/<feature>` or `adm/<feature>` follows the same shape:

```
<feature>/
  <Feature>Controller.java      # HTTP entry: auth annotations, delegates to handlers
  handler/                    # One class per API use case
    <Action>Handler.java
  <Feature>Service.java       # Optional: logic reused by multiple handlers
  repository/
    <Entity>Repository.java
  model/
    <Entity>.java
  <Feature>Mapper.java        # Optional: map between proto and model
  event/                      # Optional (usually under adm): react to sys Emitter events
```

Proto contracts live under `proto/service/src/main/resources/service/`:

- `sys/*.proto` — platform APIs.
- `adm/*.proto` — business APIs.

Generated Java types: `io.github.jinganix.admin.starter.proto.sys.*` / `proto.adm.*`.

---

## Layer responsibilities

Request flow:

```
Client → Controller → Handler → [Service] → Repository → DB
                ↓
            Emitter (side effects, after commit-worthy state changes)
                ↓
            event/* listeners (e.g. adm/overview/event)
```

### Controller

- Maps HTTP/Webpb to handler calls.
- `@PreAuthorize` and request binding only; no business rules.
- One handler dependency per controller method.
- Request **validation** (proto constraints) is exercised at this layer in tests.

### Handler (`@Component`, `handler/`)

- **One public method per class**, typically `handle(Request) → Response`.
- **Application use case**: orchestration, transaction boundary (`@Transactional`), map request → domain/persistence operations → response.
- Throws `ApiException` for business errors (`ErrorCode`).
- May call repositories directly for simple checks (e.g. `existsByCode`); delegates repeated or multi-step logic to `*Service`.
- Emits domain events via `Emitter` when other modules should react (create/update/delete of user, role, permission).

Put **new API business flow** here first. Extract to `Service` only when a second handler needs the same logic.

### Service (`@Service`)

- Logic **shared by multiple handlers** in the same feature (or closely related features), e.g. `UserService.createUser`, `RoleService.createRolePermissions`, `PermissionService.reloadApi`.
- Cross-cutting platform behavior that is not a single HTTP use case, e.g. `RoleAuthorityService` (resolve API/UI authorities for Spring Security).
- May depend on repositories and other services; avoid depending on handlers or controllers.
- Use `@Transactional` when the shared operation defines its own transaction unit.

Do **not** add a service for a one-off code path only used by one handler—keep it in the handler until reuse appears.

### Repository

- Persistence only (JOOQ): queries, insert, update, delete.
- No `ApiException`, no proto types, no HTTP/security.

### Model (`model/`)

- Persistence-oriented records (fields + enums). Not rich domain aggregates.
- Business rules live in handlers/services, not on model getters/setters.

### Mapper (`*Mapper.java`)

- Converts between proto messages and models where mapping is non-trivial.
- No branching business rules; no I/O.

---

## Events (`sys/emitter`)

Platform publishes lifecycle hooks through `Emitter` (user/role/permission created or deleted, API called).

- **Publish** from handlers (or services they call) after the main state change is persisted.
- **Subscribe** in `adm/<feature>/event/` by extending `OnUserCreated`, `OnRoleCreated`, etc.
- Listeners perform **secondary effects** (denormalized counters, notifications)—not the primary transaction path.
- When adding a new reaction to platform changes, add a listener under `adm`, not logic inside `sys` handlers.

---

## Where to put new code

| Change | Location |
|--------|----------|
| New business feature | `adm/<name>/` + `proto/.../adm/<Name>.proto` + migration + tests |
| New platform admin API | `sys/<name>/` + `proto/.../sys/<Name>.proto` |
| Reused persistence/query | `repository/` in the owning feature |
| Rule used by one endpoint | `handler/<Action>Handler.java` |
| Rule used by 2+ endpoints in same area | `<Feature>Service.java` |
| Auth/token/JWT utilities | `helper/auth/` |
| Spring wiring | `setup/config/` |

---

## Scaling beyond a single module (fork guidance)

The starter ships as one Gradle `service` module with package boundaries. When the forked product grows:

1. Keep **package** boundaries (`sys` vs `adm.<feature>`).
2. Split **Gradle subprojects** when teams need compile-time isolation (e.g. `platform-sys`, `business-order`).
3. Introduce a dedicated **domain** package inside a complex `adm` feature only when invariants and state machines justify it— not globally upfront.

---

## Test infrastructure

- `tests/SpringBootIntegrationTests.java`: integration test base.
- `tests/TestHelper.java`: HTTP/data/assertion helpers.
- `tests/TestConst.java`: test constants.

Testing rules: [BACKEND_TEST_CONVENTIONS.md](BACKEND_TEST_CONVENTIONS.md).

---

## Typical change mapping

- Business logic: update `service/src/main/...` + matching handler (or service) tests.
- Request schema/validation: update `proto/...` + API boundary (controller) tests.
- DB schema: add migration SQL + repository/handler tests.
- Side effect on platform event: add or update `adm/.../event/*` + tests if non-trivial.
