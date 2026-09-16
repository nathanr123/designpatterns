package com.example.designpatterns.creational.singleton;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Spring-managed "Singleton".
 * <p>
 * There is no private constructor and no {@code getInstance()} method here -
 * Spring beans are singleton-scoped by default within an
 * {@code ApplicationContext}. The container creates exactly one instance of
 * this bean and hands the same reference to every {@code @Autowired} field,
 * constructor, or {@code getBean()} call, so this class gets Singleton
 * behavior "for free" from the framework instead of hand-rolling it.
 * <p>
 * The trade-off: this guarantee only holds per {@code ApplicationContext}.
 * A test that spins up two contexts, or an app with multiple contexts
 * (parent/child, multiple modules), will see one instance per context, not
 * one instance per JVM like the classic/enum/holder variants below.
 */
@Component
public class ConfigurationManager {

    private final String instanceId;
    private final Instant createdAt;
    private final Map<String, String> config = new ConcurrentHashMap<>();

    public ConfigurationManager() {
        this.instanceId = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.config.put("app.name", "singleton-demo");
        this.config.put("app.variant", "spring-managed");
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
