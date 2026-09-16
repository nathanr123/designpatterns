package com.example.designpatterns.creational.singleton;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classic thread-safe Singleton using double-checked locking.
 * <p>
 * {@code instance} must be {@code volatile} so that a partially constructed
 * object (due to instruction reordering) is never visible to another thread
 * that reads {@code instance} without holding the lock.
 * <p>
 * Two loopholes the classic pattern has to guard against explicitly:
 * <ul>
 *   <li><b>Reflection</b> - calling {@code setAccessible(true)} on the
 *       private constructor lets attacker code invoke it directly. The
 *       constructor defends itself by throwing if an instance already
 *       exists, so a second reflective construction fails fast instead of
 *       silently producing a second instance.</li>
 *   <li><b>Serialization</b> - by default, deserializing a captured byte
 *       stream allocates a brand new object without ever calling the
 *       constructor. Implementing {@link #readResolve()} tells the
 *       deserialization machinery to discard that new object and substitute
 *       the existing singleton instead.</li>
 * </ul>
 */
public final class DoubleCheckedLockingSingleton implements Serializable {

    private static final long serialVersionUID = 1L;

    private static volatile DoubleCheckedLockingSingleton instance;

    private final String instanceId;
    private final Instant createdAt;
    private final Map<String, String> config = new ConcurrentHashMap<>();

    private DoubleCheckedLockingSingleton() {
        if (instance != null) {
            throw new IllegalStateException(
                    "Instance already exists - use getInstance() instead of reflection");
        }
        this.instanceId = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.config.put("app.name", "singleton-demo");
        this.config.put("app.variant", "double-checked-locking");
    }

    public static DoubleCheckedLockingSingleton getInstance() {
        DoubleCheckedLockingSingleton result = instance;
        if (result == null) {
            synchronized (DoubleCheckedLockingSingleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return result;
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

    /**
     * Called by the deserialization machinery in place of the object it just
     * allocated. Returning the existing singleton here means a deserialized
     * byte stream can never produce a second instance.
     */
    private Object readResolve() {
        return getInstance();
    }
}
