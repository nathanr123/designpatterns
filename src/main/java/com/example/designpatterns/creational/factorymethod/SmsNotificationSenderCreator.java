package com.example.designpatterns.creational.factorymethod;

/** Concrete Creator: the factory method always returns an {@link SmsNotificationSender}. */
public class SmsNotificationSenderCreator extends NotificationSenderCreator {

    @Override
    public NotificationSender createNotificationSender() {
        return new SmsNotificationSender();
    }
}
