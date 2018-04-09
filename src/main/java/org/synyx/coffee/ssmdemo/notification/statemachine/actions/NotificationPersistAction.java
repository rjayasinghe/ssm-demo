package org.synyx.coffee.ssmdemo.notification.statemachine.actions;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.notification.Notification;
import org.synyx.coffee.ssmdemo.notification.NotificationRepository;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendEvents;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendStates;


@Component
public class NotificationPersistAction implements Action<AmqpSendStates, AmqpSendEvents> {

    private final NotificationRepository notificationRepository;

    public NotificationPersistAction(NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void execute(StateContext<AmqpSendStates, AmqpSendEvents> context) {

        final String token;

        if (context.getMessage() != null && context.getMessage().getHeaders().containsKey("registration_token")) {
            token = (String) context.getMessage().getHeaders().get("registration_token");
        } else {
            token = "lost token";
        }

        // persist the notification
        notificationRepository.save(new Notification(token));
    }
}
