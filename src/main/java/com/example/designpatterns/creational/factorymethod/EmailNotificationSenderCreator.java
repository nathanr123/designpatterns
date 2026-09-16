package com.example.designpatterns.creational.factorymethod;

/** Concrete Creator: the factory method always returns an {@link EmailNotificationSender}. */
public class EmailNotificationSenderCreator extends NotificationSenderCreator {

    @Override
    public NotificationSender createNotificationSender() {
        return new EmailNotificationSender();
    }
}
