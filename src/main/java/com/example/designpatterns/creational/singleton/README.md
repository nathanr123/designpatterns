# Singleton

Four implementation strategies for the Singleton pattern, wrapped around a
realistic `ConfigurationManager` use case and exposed over REST so their
behavior can be compared side by side.

Part of the [design-patterns](../../../../../../../../README.md) project —
see the root README for how to build and run the whole application.

## Files in this package

| File | Implementation |
|---|---|
| `DoubleCheckedLockingSingleton.java` | Classic thread-safe singleton: `volatile` + double-checked locking |
| `EnumSingleton.java` | Effective Java's recommended approach: enum singleton |
| `BillPughSingleton.java` | Initialization-on-demand holder idiom |
| `ConfigurationManager.java` | `@Component` — Spring-managed singleton |
| `SingletonController.java` | REST endpoints proving instance reuse for all four variants |

## Running it

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`. Each endpoint returns the variant
name, the singleton's `instanceId` (a UUID assigned once, at construction),
its JVM `instanceHashCode` (via `System.identityHashCode`), its `createdAt`
timestamp, and a sample config value — call any endpoint repeatedly and every
field stays identical, proving the same instance is reused:

| Endpoint                                     | Implementation                          |
|-----------------------------------------------|------------------------------------------|
| `GET /api/singleton/double-checked-locking`   | Double-checked locking + `volatile`      |
| `GET /api/singleton/enum`                     | Enum singleton                           |
| `GET /api/singleton/bill-pugh`                | Static inner class holder (Bill Pugh)    |
| `GET /api/singleton/spring`                   | Spring-managed `@Component` bean         |

Run the tests with:

```bash
./gradlew test --tests "com.example.designpatterns.creational.singleton.*"
```

## Verifying with Postman

Import `singleton-demo.postman_collection.json` (project root) into Postman
(or Newman). It defines a collection variable `baseUrl` (default
`http://localhost:8080`) and a **"Singleton Verification"** folder with two
requests per endpoint — the first call stores `instanceId:instanceHashCode`
into a collection variable, and the second call asserts (`pm.test`) that the
value is identical. With the app running via `./gradlew bootRun`, run the
folder via the Collection Runner, or with Newman:

```bash
newman run singleton-demo.postman_collection.json --folder "Singleton Verification"
```

All 16 assertions (2 per request × 8 requests) should pass.

## What problem does Singleton solve?

Singleton ensures a class has **exactly one instance** and provides a single,
well-known access point to it. It's useful when a piece of state or a
resource is inherently shared and expensive/incoherent to duplicate — a
configuration registry, a connection pool, a cache, a logging sink — and
having two independent copies would mean two sources of truth or two
competing handles to the same external resource.

**Use it when:**
- Exactly one instance genuinely makes sense for the process's lifetime.
- Global access to that instance is needed, and passing it explicitly
  everywhere would be unreasonably invasive.

**Avoid it when:**
- You just want to avoid passing a dependency around — that's what
  dependency injection is for (see below).
- You need per-request, per-thread, or per-tenant state — a Singleton makes
  that state global and shared, which becomes a hidden coupling and a
  concurrency hazard.
- You need to unit test in isolation — global mutable state is hard to reset
  between tests and hard to substitute with a mock.

## Where this shows up in real systems

- **Application configuration** — this demo's own use case: one place that
  holds parsed config/feature flags, so every part of the app reads the
  same values instead of each component re-parsing a config file.
- **Connection pools** — a JDBC `DataSource` pool, a Redis client pool, an
  HTTP client's connection pool. Two independent pools to the same backend
  would double the connection count for no benefit and make pool-size
  limits meaningless.
- **Loggers and metrics registries** — `java.util.logging.Logger` instances
  per class, or a Micrometer `MeterRegistry`: every part of the app should
  emit logs/metrics through the same sink so aggregation and correlation
  work.
- **Caches** — an in-memory cache (e.g. a Caffeine `Cache` instance) is only
  useful as a cache if there's one of it; a second instance would just be a
  second, inconsistent copy of the same data.
- **Hardware/driver handles** — a print spooler, a GPU context, a serial
  port handle: the underlying resource is physically singular, so the
  in-process representation of it should be too.

In a Spring app specifically, all five of the above are typically just
`@Component`/`@Bean` singletons (Spring's default scope) — see
[Why Spring often makes the classic GoF Singleton unnecessary](#why-spring-often-makes-the-classic-gof-singleton-unnecessary)
below for when you'd still reach for a hand-rolled one instead.

## Trade-offs across the four implementations

| Aspect | Double-checked locking | Enum | Bill Pugh (holder) | Spring-managed |
|---|---|---|---|---|
| **Initialization** | Lazy (created on first `getInstance()`) | Eager, at enum class load | Lazy (holder class loads on first access) | Lazy or eager depending on scope/`@Lazy`; container-controlled |
| **Thread-safety mechanism** | Explicit: `volatile` + `synchronized` block | Implicit: JVM guarantees atomic, one-time class init | Implicit: JLS guarantees thread-safe class initialization | Container-managed; scope/bean lifecycle handled by Spring |
| **Thread-safety cost** | Small (one `volatile` read on the fast path after warm-up) | None at call time | None at call time | None at call time (resolved once, cached in the context) |
| **Reflection breakage** | Guarded — the constructor throws `IllegalStateException` if an instance already exists, so a `Constructor.setAccessible(true)` call fails instead of silently creating a second instance | Immune — the JVM forbids reflective enum instantiation | Guarded — same `instanceCreated` check, since there's no `instance` field on the outer class to inspect | Not applicable — Spring doesn't hide the constructor, so reflection isn't "breaking" anything; it can just make more beans if you ask it to |
| **Serialization breakage** | Guarded — implements `Serializable` with `readResolve()`, so deserializing a captured byte stream returns the existing instance instead of a new one | Immune — enum serialization is handled specially by the JVM (writes only the name) | Guarded — same `readResolve()` pattern | Not typically serialized directly; irrelevant to bean identity |
| **Testability** | Poor — static state persists across tests in the same JVM; hard to reset/mock | Poor — same static-state problem, plus can't be subclassed or easily faked | Poor — same static-state problem as double-checked locking | Good — Spring Test can swap the bean via `@MockBean`/test configuration, or spin up a fresh `ApplicationContext` per test class |

### Hardening the manual singletons

`DoubleCheckedLockingSingleton` and `BillPughSingleton` both guard against
the two classic ways to defeat a hand-rolled singleton:

- **Reflection** — `Constructor.setAccessible(true)` lets code outside the
  class call a private constructor directly. Both constructors check for an
  existing instance (`instance != null` for the double-checked variant, a
  dedicated `instanceCreated` flag for the holder variant, since it has no
  `instance` field on the outer class to check) and throw
  `IllegalStateException` on a second call, so reflective construction fails
  loudly instead of quietly producing a second instance.
- **Serialization** — by default, Java deserialization allocates a new
  object without invoking any constructor, bypassing the reflection guard
  entirely. Both classes implement `Serializable` and add a private
  `readResolve()` that returns `getInstance()`, so the deserialization
  machinery discards the object it just allocated and substitutes the real
  singleton.

`EnumSingleton` needs neither: the JVM enforces both guarantees for enums
natively. `ConfigurationManager` needs neither either, but for a different
reason — cardinality is enforced by the container, not by hiding the
constructor, so there's nothing for reflection or serialization to defeat.
`SingletonHardeningTest` exercises both guards on both manual singletons.

## Why Spring often makes the classic GoF Singleton unnecessary

Spring beans are **singleton-scoped by default**: for any given
`ApplicationContext`, the container creates one instance of a bean and hands
out that same reference to every injection point (`@Autowired` field,
constructor parameter, `getBean()` call). You get:

- Thread-safe, lazy-or-eager (configurable) instantiation for free — no
  hand-written `synchronized`/`volatile` code to get right.
- Testability — swap the bean for a test double via `@MockBean`, a test
  `@Configuration`, or a differently-scoped context, without touching the
  production class.
- No serialization/reflection loopholes to guard against, because nothing
  about the class itself claims "there can only be one" — the *container*
  enforces the cardinality, not the class.

This is why, inside a Spring application, the idiomatic way to get
Singleton-like behavior is simply `@Component`/`@Service` and constructor
injection — as demonstrated by `ConfigurationManager` here — rather than
hand-rolling `getInstance()`.

**When you'd still hand-roll a classic/enum/holder singleton even in a
Spring app:**
- The class needs to be a true JVM-wide singleton usable *outside* any
  Spring context — e.g. in a static utility, a Java agent, a non-Spring
  library, or code that must work before the `ApplicationContext` exists.
- You need the guarantee to hold across **multiple** `ApplicationContext`s
  in the same JVM (parent/child contexts, multi-module tests) where Spring's
  "one instance per context" guarantee isn't strong enough.
- You're protecting against adversarial reflection/deserialization and want
  the strongest guarantee the JVM offers — this is exactly what the
  Enum Singleton is for.

## Diagrams

### Class diagram

```mermaid
classDiagram
    class DoubleCheckedLockingSingleton {
        -static volatile DoubleCheckedLockingSingleton instance
        -String instanceId
        -Instant createdAt
        -DoubleCheckedLockingSingleton()
        +static getInstance() DoubleCheckedLockingSingleton
        +getProperty(key) String
    }

    class EnumSingleton {
        <<enumeration>>
        INSTANCE
        -String instanceId
        -Instant createdAt
        +getProperty(key) String
    }

    class BillPughSingleton {
        -String instanceId
        -Instant createdAt
        -BillPughSingleton()
        +static getInstance() BillPughSingleton
        +getProperty(key) String
    }

    class Holder {
        -static final BillPughSingleton INSTANCE
    }

    class ConfigurationManager {
        <<@Component>>
        -String instanceId
        -Instant createdAt
        +ConfigurationManager()
        +getProperty(key) String
    }

    class SingletonController {
        -ConfigurationManager configurationManager
        +doubleCheckedLocking() SingletonProofResponse
        +enumSingleton() SingletonProofResponse
        +billPugh() SingletonProofResponse
        +spring() SingletonProofResponse
    }

    BillPughSingleton *-- Holder : holds instance
    SingletonController --> DoubleCheckedLockingSingleton : getInstance()
    SingletonController --> EnumSingleton : INSTANCE
    SingletonController --> BillPughSingleton : getInstance()
    SingletonController --> ConfigurationManager : constructor-injected
```

### Sequence diagram (lazy variants: double-checked locking / Bill Pugh)

```mermaid
sequenceDiagram
    participant Client as Client (REST call #1)
    participant Ctrl as SingletonController
    participant Singleton as DoubleCheckedLockingSingleton

    Client->>Ctrl: GET /api/singleton/double-checked-locking
    Ctrl->>Singleton: getInstance()
    Note over Singleton: instance is null
    Singleton->>Singleton: synchronized block, create new instance
    Singleton-->>Ctrl: newly created instance
    Ctrl-->>Client: instanceId=A, hashCode=H1

    participant Client2 as Client (REST call #2)
    Client2->>Ctrl: GET /api/singleton/double-checked-locking
    Ctrl->>Singleton: getInstance()
    Note over Singleton: instance already set, return immediately
    Singleton-->>Ctrl: same existing instance
    Ctrl-->>Client2: instanceId=A, hashCode=H1 (identical)
```

## Notes on the manual singletons and Spring

`DoubleCheckedLockingSingleton`, `EnumSingleton`, and `BillPughSingleton` are
deliberately **not** Spring beans — they manage their own instance the
classic way, independent of any `ApplicationContext`, so the demo can
contrast them directly against `ConfigurationManager`, which relies entirely
on the container's default singleton scope.
