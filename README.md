# design-patterns

A single Java 25 + Spring Boot 3 Gradle project for design pattern demos.
Pattern *categories* (Creational, Behavioral, Structural) are Java packages,
not separate folders or separate Gradle projects — this keeps everything on
one build, one dependency set, and one running application.

Each pattern has its own README.md next to its code, alongside the pattern's
own diagrams and trade-off discussion. This file is just an index.

## Project layout

```
design-patterns/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/wrapper/gradle-wrapper.properties
├── gradle/wrapper/gradle-wrapper.jar
├── singleton-demo.postman_collection.json
├── README.md
└── src/
    ├── main/java/com/example/designpatterns/
    │   ├── DesignPatternsApplication.java     # single @SpringBootApplication entry point
    │   ├── creational/
    │   │   ├── singleton/
    │   │   │   ├── README.md                           # Singleton explanation + diagrams
    │   │   │   ├── DoubleCheckedLockingSingleton.java   # classic thread-safe singleton
    │   │   │   ├── EnumSingleton.java                   # Effective Java recommended approach
    │   │   │   ├── BillPughSingleton.java               # static inner class holder idiom
    │   │   │   ├── ConfigurationManager.java            # @Component, Spring-managed singleton
    │   │   │   └── SingletonController.java             # REST endpoints for all 4 variants
    │   │   └── factorymethod/
    │   │       ├── README.md                            # Factory Method explanation + diagrams
    │   │       ├── NotificationSender.java               # Product interface
    │   │       ├── EmailNotificationSender.java          # Concrete Product (@Component)
    │   │       ├── SmsNotificationSender.java            # Concrete Product (@Component)
    │   │       ├── PushNotificationSender.java           # Concrete Product (@Component)
    │   │       ├── NotificationSenderCreator.java        # Abstract Creator
    │   │       ├── EmailNotificationSenderCreator.java   # Concrete Creator
    │   │       ├── SmsNotificationSenderCreator.java     # Concrete Creator
    │   │       ├── PushNotificationSenderCreator.java    # Concrete Creator
    │   │       ├── NotificationSenderRegistry.java       # Spring map-based factory alternative
    │   │       ├── NotificationSenderFactoryConfig.java  # @Configuration wiring the injected Map
    │   │       └── NotificationController.java           # REST endpoint using the factory method
    │   ├── behavioral/
    │   │   └── package-info.java              # empty; future patterns land here
    │   └── structural/
    │       └── package-info.java              # empty; future patterns land here
    └── test/java/com/example/designpatterns/creational/
        ├── singleton/
        │   ├── ManualSingletonThreadSafetyTest.java
        │   ├── SpringSingletonBeanTest.java
        │   └── SingletonHardeningTest.java
        └── factorymethod/
            ├── CreatorFactoryMethodTest.java
            └── SpringNotificationSenderRegistryTest.java
```

**How future patterns get added:** when Observer, Adapter, etc. are
implemented, they become new classes (and, if needed, new subpackages) inside
the existing `com.example.designpatterns.behavioral` and
`com.example.designpatterns.structural` packages — never new Gradle projects
or new top-level folders. Each new pattern gets its own README.md next to its
code, and gets a row added to the table below. The whole repository stays
one buildable, runnable, testable Spring Boot application.

## Running it

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.

```bash
./gradlew test
```

## Verifying with Postman

Import `singleton-demo.postman_collection.json` into Postman (or Newman).
It defines a collection variable `baseUrl` (default `http://localhost:8080`)
and one folder per pattern, each with `pm.test` assertions. See each
pattern's own README for details on what that folder verifies.

```bash
newman run singleton-demo.postman_collection.json
```

## Patterns implemented

| Pattern | Category | Docs |
|---|---|---|
| Singleton | Creational | [singleton/README.md](src/main/java/com/example/designpatterns/creational/singleton/README.md) |
| Factory Method | Creational | [factorymethod/README.md](src/main/java/com/example/designpatterns/creational/factorymethod/README.md) |
