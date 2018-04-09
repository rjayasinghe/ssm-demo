package org.synyx.coffee.ssmdemo.notification.confirmation;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.context.event.EventListener;

import org.springframework.messaging.Message;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.notification.NotificationRepository;
import org.synyx.coffee.ssmdemo.notification.events.ConfirmationCreateEvent;
import org.synyx.coffee.ssmdemo.notification.events.NoRouteForMessageEvent;
import org.synyx.coffee.ssmdemo.notification.events.PublishConfirmedEvent;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendEvents;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendStates;


@Component
public class ConfirmationHandler implements Loggable {

    private static final String STATE_MACHINE_ID_PREFIX = "confirmation_%s";
    private final StateMachineService<AmqpSendStates, AmqpSendEvents> amqpSendStateMachineService;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;

    public ConfirmationHandler(StateMachineService<AmqpSendStates, AmqpSendEvents> amqpSendStateMachineService,
        RabbitTemplate rabbitTemplate, NotificationRepository notificationRepository) {

        this.amqpSendStateMachineService = amqpSendStateMachineService;
        this.rabbitTemplate = rabbitTemplate;
        this.notificationRepository = notificationRepository;
    }

    @EventListener
    @Transactional
    @Async
    public void on(ConfirmationCreateEvent confirmationCreateEvent) {

        final String machineId = createMachineIdFromToken(confirmationCreateEvent.getToken());

        sendEventToStateMachine(AmqpSendEvents.START, confirmationCreateEvent.getToken(), true);
    }


    @EventListener
    @Async
    @Transactional
    public void on(NoRouteForMessageEvent noRouteForMessageEvent) {

        final String token = noRouteForMessageEvent.getMessageId();

        sendEventToStateMachine(AmqpSendEvents.NOT_ROUTED, token, false);

        logger().info("got not routed event for token {}", token);
    }


    @EventListener
    @Async
    @Transactional
    public void on(PublishConfirmedEvent publishConfirmedEvent) {

        final String token = publishConfirmedEvent.getMessageId();
        sendEventToStateMachine(AmqpSendEvents.PUBLISH_CONFIRMED, token, false);
        logger().info("got publish confirmed event for token {}", token);
    }


    @Scheduled(fixedDelay = 5000L)
    public void reschedulePotentiallyFailedNotifications() {

        logger().info("checking for open notifications and notify their statemachines to retry send");
        notificationRepository.findAll().forEach(notification -> {
            sendEventToStateMachine(AmqpSendEvents.SEND, notification.getToken(), false);
        });
    }


    private void sendEventToStateMachine(AmqpSendEvents event, String token, boolean startMachine) {

        StateMachine<AmqpSendStates, AmqpSendEvents> amqpSendStateMachine =
            amqpSendStateMachineService.acquireStateMachine(createMachineIdFromToken(token), startMachine);

        amqpSendStateMachine.sendEvent(createEventMessage(event, token));
    }


    private Message<AmqpSendEvents> createEventMessage(AmqpSendEvents event, String registrationToken) {

        return org.springframework.messaging.support.MessageBuilder.withPayload(event)
            .setHeader("registration_token", registrationToken)
            .build();
    }


    private String createMachineIdFromToken(String machineId) {

        return String.format(STATE_MACHINE_ID_PREFIX, machineId);
    }
}
