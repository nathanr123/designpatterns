package com.example.designpatterns.creational.singleton;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enum-based Singleton - the approach recommended in Effective Java (Item 3).
 * <p>
 * The JVM guarantees that each enum constant is instantiated exactly once,
 * even under concurrent classloading, and enum serialization is handled by
 * the JVM itself (only the name is written), so this form is immune to the
 * reflection and serialization attacks that break the classic pattern.
 */
public enum EnumSingleton {

    INSTANCE;

    private final String instanceId;
    private final Instant createdAt;
    private final Map<String, String> config = new ConcurrentHashMap<>();

    EnumSingleton() {
        this.instanceId = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.config.put("app.name", "singleton-demo");
        this.config.put("app.variant", "enum");
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getProperty(String key) {
        return config.get(key);
    }

    public void setProperty(String key, String value) {
        config.put(key, value);
    }
}
