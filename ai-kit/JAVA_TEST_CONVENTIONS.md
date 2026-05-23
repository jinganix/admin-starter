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

Example:

```java
@Nested
@DisplayName("create")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Create {

  private Stream<FooCreateRequest> invalidRequests() {
    return Stream.of(
        new FooCreateRequest(null, "bar"),
        new FooCreateRequest("ab", "bar"),
        new FooCreateRequest("foo", ""));
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  @DisplayName("Given invalid request -> response BAD_REQUEST")
  void givenInvalidRequest(FooCreateRequest request) throws Exception {
    testHelper.expectError(testHelper.request(request), ErrorCode.BAD_REQUEST);
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
- Invalid requests: parameterized with request objects.
- Authorization checks follow validation checks.
- One success case per controller method.
