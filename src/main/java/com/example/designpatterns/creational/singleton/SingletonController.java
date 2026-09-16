package com.example.designpatterns.creational.singleton;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes one endpoint per Singleton variant. Calling the same endpoint
 * repeatedly must always return the same instanceId/hashCode/createdAt,
 * proving the underlying instance is reused rather than recreated.
 */
@RestController
@RequestMapping("/api/singleton")
public class SingletonController {

    private final ConfigurationManager configurationManager;

    public SingletonController(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @GetMapping("/double-checked-locking")
    public SingletonProofResponse doubleCheckedLocking() {
        DoubleCheckedLockingSingleton instance = DoubleCheckedLockingSingleton.getInstance();
        return new SingletonProofResponse(
                "double-checked-locking",
                instance.getInstanceId(),
                System.identityHashCode(instance),
                instance.getCreatedAt(),
                instance.getProperty("app.variant"));
    }

    @GetMapping("/enum")
    public SingletonProofResponse enumSingleton() {
        EnumSingleton instance = EnumSingleton.INSTANCE;
        return new SingletonProofResponse(
                "enum",
                instance.getInstanceId(),
                System.identityHashCode(instance),
                instance.getCreatedAt(),
                instance.getProperty("app.variant"));
    }

    @GetMapping("/bill-pugh")
    public SingletonProofResponse billPugh() {
        BillPughSingleton instance = BillPughSingleton.getInstance();
        return new SingletonProofResponse(
                "bill-pugh-holder",
                instance.getInstanceId(),
                System.identityHashCode(instance),
                instance.getCreatedAt(),
                instance.getProperty("app.variant"));
    }

    @GetMapping("/spring")
    public SingletonProofResponse spring() {
        return new SingletonProofResponse(
                "spring-managed",
                configurationManager.getInstanceId(),
                System.identityHashCode(configurationManager),
                configurationManager.getCreatedAt(),
                configurationManager.getProperty("app.variant"));
    }

    /**
     * Proof-of-singleton payload: the same instanceId, createdAt, and
     * instanceHashCode must be returned on every call to a given endpoint,
     * regardless of how many times it is hit.
     */
    public record SingletonProofResponse(
            String variant,
            String instanceId,
            int instanceHashCode,
            Instant createdAt,
            String configValue
    ) {
    }
}
