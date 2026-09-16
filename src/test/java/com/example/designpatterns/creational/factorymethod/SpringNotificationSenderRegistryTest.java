package com.example.designpatterns.creational.factorymethod;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SpringNotificationSenderRegistryTest {

    @Autowired
    private NotificationSenderRegistry registry;

    @Test
    void resolvesEmailChannelToEmailSenderBean() {
        assertInstanceOf(EmailNotificationSender.class, registry.resolve("email"));
    }

    @Test
    void resolvesSmsChannelToSmsSenderBean() {
        assertInstanceOf(SmsNotificationSender.class, registry.resolve("sms"));
    }

    @Test
    void resolvesPushChannelToPushSenderBean() {
        assertInstanceOf(PushNotificationSender.class, registry.resolve("push"));
    }

    @Test
    void unknownChannelThrowsInsteadOfReturningNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> registry.resolve("carrier-pigeon"));

        assertInstanceOf(String.class, exception.getMessage());
    }
}
