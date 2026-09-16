package com.example.designpatterns.creational.factorymethod;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires up Spring's registry-based alternative to the GoF Factory Method.
 * Spring automatically collects every {@link NotificationSender} bean into
 * a {@code Map<String, NotificationSender>} keyed by bean name (see the
 * {@code @Component("email")}/{@code ("sms")}/{@code ("push")} annotations
 * on the concrete Products) and injects that map here with no extra wiring.
 */
@Configuration
public class NotificationSenderFactoryConfig {

    @Bean
    public NotificationSenderRegistry notificationSenderRegistry(Map<String, NotificationSender> sendersByChannel) {
        return new NotificationSenderRegistry(sendersByChannel);
    }
}
