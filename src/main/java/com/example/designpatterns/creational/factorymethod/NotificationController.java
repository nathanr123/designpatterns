package com.example.designpatterns.creational.factorymethod;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dispatches a (mocked) notification over a channel chosen at runtime,
 * using the classic GoF Factory Method: the controller picks a concrete
 * {@link NotificationSenderCreator} for the requested channel, and the
 * Creator decides which concrete {@link NotificationSender} to build.
 * <p>
 * See {@link NotificationSenderRegistry} for Spring's map-based alternative
 * to this same channel-to-implementation lookup.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping("/{channel}/send")
    public NotificationResponse send(@PathVariable String channel, @RequestBody NotificationRequest request) {
        NotificationSenderCreator creator = resolveCreator(channel);
        NotificationSender sender = creator.createNotificationSender();
        String result = sender.send(request.recipient(), request.message());
        return new NotificationResponse(channel, sender.getClass().getSimpleName(), result);
    }

    private NotificationSenderCreator resolveCreator(String channel) {
        return switch (channel.toLowerCase()) {
            case "email" -> new EmailNotificationSenderCreator();
            case "sms" -> new SmsNotificationSenderCreator();
            case "push" -> new PushNotificationSenderCreator();
            default -> throw new NoSuchElementException("Unknown notification channel: '" + channel + "'");
        };
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnknownChannel(NoSuchElementException ex) {
        return Map.of("error", ex.getMessage());
    }

    public record NotificationRequest(String recipient, String message) {
    }

    public record NotificationResponse(String channel, String handledBy, String result) {
    }
}
