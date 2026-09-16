package com.example.designpatterns.creational.factorymethod;

/**
 * Factory Method "Product": something that can deliver a notification over
 * one specific channel. Concrete implementations don't know or care how
 * they get chosen — that's the Creator's job.
 */
public interface NotificationSender {

    /**
     * Sends (in this demo: mocks sending) a notification and returns a
     * human-readable description of what happened, so callers/tests can
     * confirm which concrete implementation actually ran.
     */
    String send(String recipient, String message);
}
