package com.example.designpatterns.creational.factorymethod;

/** Concrete Creator: the factory method always returns an {@link PushNotificationSender}. */
public class PushNotificationSenderCreator extends NotificationSenderCreator {

    @Override
    public NotificationSender createNotificationSender() {
        return new PushNotificationSender();
    }
}
