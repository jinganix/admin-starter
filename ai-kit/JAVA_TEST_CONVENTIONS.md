# Java backend testing conventions

> Read this document before editing tests under `service/src/test/**`.
> Run tests from repository root: `./gradlew :service:test`

---

## 1. Scope

- Test classes with real logic (`*Handler`, branching `*Service`, controller validation/auth paths).
- Skip pure model/mapping/pass-through code without branches.

---

## 2. Layout and naming

- Test package mirrors source package.
- Test class: `{ClassName}Test`.
- Single-entry handler test method names: `given{Scenario}`.
- Use `@DisplayName("Given ... -> ...")` for handler tests.

---

## 3. Handler tests

For classes with only one public business entry (usually `handle`):

- Put all tests directly on outer test class.
- Do **not** use `@Nested`.
- Keep `// Given`, `// When`, `// Then` comments.
- Cover at least:
  - one failure path
  - one success path
- Handler tests cover business logic only.
- Do **not** place request DTO validation tests (`@NotNull`, `@Size`, `@Pattern`) in handler tests.

Example:

```java
@DisplayName("FooHandler")
class FooHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;
  @Autowired FooHandler fooHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given missing foo -> throw ApiException")
  void givenMissingFoo() {
    // Given
    FooRequest request = new FooRequest("foo");

    // When / Then
    assertThatThrownBy(() -> fooHandler.handle(request)).isInstanceOf(ApiException.class);
  }
}
```

---

## 4. Controller tests

- Use one `@Nested` class per controller method.
- Keep test methods flat inside each method-level `@Nested`.
- Place request validation checks before authorization checks.
- Add authorization checks after validation checks.
- Keep one success case per controller method.
- Move extra business branches to handler tests.

### 4.1 Request validation

- Use `@ParameterizedTest` + `@MethodSource` with request objects.
- Put the source method directly above the related parameterized test.
- Cover representative boundaries per constrained field:
  - `null` / blank
  - below min
  - above max
  - pattern mismatch (if present)

Each invalid case is an `InvalidRequestCase` with **expected error**, **request**, and **description** (shown as the parameterized test name).

Example:

```java
import static io.github.jinganix.admin.starter.tests.InvalidRequestCase.badRequest;

@Nested
@DisplayName("create")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Create {

  private Stream<InvalidRequestCase<FooCreateRequest>> invalidRequests() {
    return Stream.of(
        badRequest(new FooCreateRequest(null, "bar"), "name is null"),
        badRequest(new FooCreateRequest("ab", "bar"), "name below min length (3)"),
        badRequest(new FooCreateRequest("foo", ""), "code is blank"));
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void givenInvalidRequest(InvalidRequestCase<FooCreateRequest> testCase) throws Exception {
    testHelper.expectError(testHelper.request(testCase.request()), testCase.errorCode());
  }
}
```

---

## 5. Integration test base rules

- Extend `tests.SpringBootIntegrationTests`.
- In `@BeforeEach`, call `testHelper.clearAll()`.
- Keep `@MockitoSpyBean` only in the base class (never in subclasses).

---

## 6. Assertions and helpers

- Use AssertJ (`assertThat`, `assertThatThrownBy`).
- HTTP errors: prefer `testHelper.expectError(...)`.
- HTTP success payloads: use `testHelper.isResponse(...)` (recursive comparison).

---

## 7. Commands

```bash
./gradlew :service:test
./gradlew :service:test --tests "com.example.module.FooHandlerTest"
```

---

## 8. Quick checklist

- Handler tests: business logic only, no request validation checks.
- Single-entry handler: flat outer tests, no `@Nested`.
- Controller tests: method-level `@Nested`, flat inner tests.
- Invalid requests: parameterized with `InvalidRequestCase` (error + request + description).
- Authorization checks follow validation checks.
- One success case per controller method.
