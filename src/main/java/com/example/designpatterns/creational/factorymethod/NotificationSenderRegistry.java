package com.example.designpatterns.creational.factorymethod;

import java.util.Map;
import java.util.TreeSet;

/**
 * Spring's alternative to the GoF Factory Method: instead of a hierarchy of
 * Creator subclasses, Spring already knows every {@link NotificationSender}
 * bean and can inject them all at once, keyed by bean name, into a single
 * {@code Map<String, NotificationSender>}. This class just wraps that map
 * with a lookup that fails loudly (instead of returning {@code null}) for
 * an unknown channel.
 */
public class NotificationSenderRegistry {

    private final Map<String, NotificationSender> sendersByChannel;

    public NotificationSenderRegistry(Map<String, NotificationSender> sendersByChannel) {
        this.sendersByChannel = Map.copyOf(sendersByChannel);
    }

    public NotificationSender resolve(String channel) {
        NotificationSender sender = sendersByChannel.get(channel.toLowerCase());
        if (sender == null) {
            throw new IllegalArgumentException(
                    "Unknown notification channel '%s'. Known channels: %s"
                            .formatted(channel, new TreeSet<>(sendersByChannel.keySet())));
        }
        return sender;
    }
}
