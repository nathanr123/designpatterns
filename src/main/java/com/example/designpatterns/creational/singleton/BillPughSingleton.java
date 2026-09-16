package com.example.designpatterns.creational.singleton;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bill Pugh Singleton (initialization-on-demand holder idiom).
 * <p>
 * The JVM only initializes a class when it is first referenced, and class
 * initialization is guaranteed by the JLS to be thread-safe without any
 * explicit locking. Because {@code Holder} is a separate class, it is not
 * loaded until {@link #getInstance()} is called, giving lazy initialization
 * with none of the synchronization overhead of double-checked locking.
 * <p>
 * Two loopholes the holder idiom has to guard against explicitly:
 * <ul>
 *   <li><b>Reflection</b> - calling {@code setAccessible(true)} on the
 *       private constructor lets attacker code invoke it directly, bypassing
 *       {@code Holder} entirely. Since there is no {@code instance} field on
 *       the outer class to check, the constructor instead flips a dedicated
 *       {@code instanceCreated} guard and throws if it was already set.</li>
 *   <li><b>Serialization</b> - by default, deserializing a captured byte
 *       stream allocates a brand new object without ever calling the
 *       constructor. Implementing {@link #readResolve()} tells the
 *       deserialization machinery to discard that new object and substitute
 *       the existing singleton instead.</li>
 * </ul>
 */
public final class BillPughSingleton implements Serializable {

    private static final long serialVersionUID = 1L;

    private static volatile boolean instanceCreated = false;

    private final String instanceId;
    private final Instant createdAt;
    private final Map<String, String> config = new ConcurrentHashMap<>();

    private BillPughSingleton() {
        if (instanceCreated) {
            throw new IllegalStateException(
                    "Instance already exists - use getInstance() instead of reflection");
        }
        instanceCreated = true;
        this.instanceId = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.config.put("app.name", "singleton-demo");
        this.config.put("app.variant", "bill-pugh-holder");
    }

    private static final class Holder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;
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
