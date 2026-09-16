package com.example.designpatterns.creational.factorymethod;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatorFactoryMethodTest {

    @Test
    void emailCreatorProducesEmailSender() {
        assertInstanceOf(EmailNotificationSender.class, new EmailNotificationSenderCreator().createNotificationSender());
    }

    @Test
    void smsCreatorProducesSmsSender() {
        assertInstanceOf(SmsNotificationSender.class, new SmsNotificationSenderCreator().createNotificationSender());
    }

    @Test
    void pushCreatorProducesPushSender() {
        assertInstanceOf(PushNotificationSender.class, new PushNotificationSenderCreator().createNotificationSender());
    }

    @Test
    void dispatchDelegatesToTheCreatedProduct() {
        String result = new EmailNotificationSenderCreator().dispatch("user@example.com", "hello");

        assertTrue(result.contains("user@example.com") && result.contains("hello"));
    }
}
