package com.example.designpatterns.creational.factorymethod;

import org.springframework.stereotype.Component;

/**
 * Concrete Product. Also registered as a Spring bean under the name
 * "email" so {@code Map<String, NotificationSender>} injection (see
 * {@link NotificationSenderFactoryConfig}) can resolve it by channel.
 */
@Component("email")
public class EmailNotificationSender implements NotificationSender {

    @Override
    public String send(String recipient, String message) {
        return "Email sent to %s: \"%s\"".formatted(recipient, message);
    }
}
