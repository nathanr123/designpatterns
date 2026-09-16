package com.example.designpatterns.creational.factorymethod;

import org.springframework.stereotype.Component;

/**
 * Concrete Product. Also registered as a Spring bean under the name
 * "sms" so {@code Map<String, NotificationSender>} injection (see
 * {@link NotificationSenderFactoryConfig}) can resolve it by channel.
 */
@Component("sms")
public class SmsNotificationSender implements NotificationSender {

    @Override
    public String send(String recipient, String message) {
        return "SMS sent to %s: \"%s\"".formatted(recipient, message);
    }
}
