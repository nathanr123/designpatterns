package com.example.designpatterns.creational.factorymethod;

/**
 * Factory Method "Creator". Owns the workflow that needs a
 * {@link NotificationSender} ({@link #dispatch}), but defers the decision
 * of which concrete Product to instantiate to subclasses via
 * {@link #createNotificationSender()}.
 * <p>
 * This is the classic GoF Factory Method: it is a plain Java class, not a
 * Spring bean, and is instantiated directly by client code (see
 * {@code NotificationController}) rather than resolved from a container.
 */
public abstract class NotificationSenderCreator {

    public abstract NotificationSender createNotificationSender();

    public String dispatch(String recipient, String message) {
        NotificationSender sender = createNotificationSender();
        return sender.send(recipient, message);
    }
}
