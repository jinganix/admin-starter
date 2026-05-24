# Backend testing conventions

> Read this document before editing tests under `service/src/test/**`.
> Run tests from repository root: `./gradlew :service:test`

---

## 1. Scope

- Test classes with real logic (`*Handler`, branching `*Service`, controller validation/auth paths).
- Skip pure model/mapping/pass-through code without branches.

---

## 2. Behavior-first organization

Applies to **all** test types (handler, service, controller):

- Organize tests around **observable behavior** (outcome + condition), not around mirroring source method names (`foo`, `bar`, `create`, `login`).
- Prefer flat `@Test` methods on the outer class. Use `@Nested` only when a group shares setup or clearly scopes one behavior slice.
- At most **one** `@Nested` level. Do not nest further for sub-scenarios.
- `@Nested` `@DisplayName` and test names use BDD wording (`when ...`, `should ... when ...`), not Java method names as the primary structure.
- Do **not** require one `@Nested` class per public method on the class under test.

---

## 3. Layout and naming

- Test package mirrors source package.
- Test class: `{ClassName}Test`.
- Prefer concise BDD format for `@DisplayName`: `should ... when ...`.
- Keep `@DisplayName` text lowercase at the start (do not start with uppercase).
- Test method names: `shouldReturn{Outcome}When{Condition}` (or `shouldThrow...When...`).

---

## 4. Handler tests

For classes with only one public business entry (usually `handle`):

- Put all tests directly on the outer test class (no `@Nested`).
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
  @DisplayName("should throw ApiException when foo is missing")
  void shouldThrowApiExceptionWhenFooIsMissing() {
    // Given
    FooRequest request = new FooRequest("foo");

    // When / Then
    assertThatThrownBy(() -> fooHandler.handle(request)).isInstanceOf(ApiException.class);
  }
}
```

---

## 5. Service tests

For branching `*Service` classes (multiple public methods or internal branches):

- Focus on **business outcomes** visible through the service API (return value, exceptions, persisted state).
- Keep tests flat by default; add one `@Nested` group only when several cases share the same Given setup.
- Group by behavior (for example `when account is locked`), not by Java method name alone.
- Prefer `@Autowired` service + direct calls; use integration base when the service touches DB or collaborators.
- Cover representative branches per behavior, not every method signature for its own sake.

---

## 6. API boundary tests (controllers)

HTTP/MockMvc tests for `*Controller` (validation, auth, status codes, response shape):

- Organize by **user- or API-visible behavior** (for example `when request body is invalid`, `when caller is not authorized`), not by one `@Nested` per controller method.
- Keep `@Test` methods flat inside any behavior group.
- Place request validation checks before authorization checks.
- Keep **one happy-path success case per behavior** under test; move extra business branches to handler or service tests.
- Use `testHelper.request(...)` for HTTP calls.

### 6.1 Request validation

- Use `@ParameterizedTest` + `@MethodSource` with request objects.
- Put the source method directly above the related parameterized test.
- Cover representative boundaries per constrained field:
  - `null` / blank
  - below min
  - above max
  - pattern mismatch (if present)

Each invalid case is an `InvalidRequestCase` with **expected error**, **request**, and **description** (shown as the parameterized test name).
Descriptions should prefer concise BDD wording (`should ... when ...`).

Example:

```java
import static io.github.jinganix.admin.starter.tests.InvalidRequestCase.badRequest;

@Nested
@DisplayName("when create request is invalid")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenCreateRequestIsInvalid {

  private Stream<InvalidRequestCase<FooCreateRequest>> invalidRequests() {
    return Stream.of(
        badRequest(new FooCreateRequest(null, "bar"), "should return bad request when name is null"),
        badRequest(
            new FooCreateRequest("ab", "bar"), "should return bad request when name below min length (3)"),
        badRequest(new FooCreateRequest("foo", ""), "should return bad request when code is blank"));
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<FooCreateRequest> testCase)
      throws Exception {
    testHelper.expectError(testHelper.request(testCase.request()), testCase.errorCode());
  }
}
```

---

## 7. Integration test base rules

- Extend `tests.SpringBootIntegrationTests`.
- In `@BeforeEach`, call `testHelper.clearAll()`.
- Keep `@MockitoSpyBean` only in the base class (never in subclasses).

---

## 8. Assertions and helpers

- Use AssertJ (`assertThat`, `assertThatThrownBy`).
- HTTP errors: prefer `testHelper.expectError(...)`.
- HTTP success payloads: use `testHelper.isResponse(...)` (recursive comparison).

---

## 9. Commands

```bash
./gradlew :service:test
./gradlew :service:test --tests "com.example.module.FooHandlerTest"
```

---

## 10. Quick checklist

- Behavior-first: names and structure reflect outcomes/conditions, not a 1:1 map to source methods.
- Handler tests: business logic only, no request validation checks; flat outer tests, no `@Nested`.
- Service tests: branch coverage via observable behavior; flat by default.
- API boundary tests: validation before auth; parameterized `InvalidRequestCase` for invalid requests.
- Prefer short BDD descriptions: `should ... when ...`.
- One happy-path success per behavior; deeper branches belong in handler/service tests.
