package org.synyx.coffee.ssmdemo.registration.statemachine;

import org.springframework.context.event.EventListener;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import org.springframework.scheduling.annotation.Async;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.registration.events.RegistrationAcceptedEvent;
import org.synyx.coffee.ssmdemo.registration.events.RegistrationCreatedEvent;


@Component
public class RegistrationEventHandler {

    private static final String STATE_MACHINE_ID_PREFIX = "registration_%s";
    private final StateMachineService<RegistrationStates, RegistrationEvents> stateMachineService;

    public RegistrationEventHandler(StateMachineService<RegistrationStates, RegistrationEvents> stateMachineService) {

        this.stateMachineService = stateMachineService;
    }

    @Async
    @EventListener
    @Transactional
    public void on(RegistrationAcceptedEvent registrationAcceptedEvent) {

        sendEventToStateMachine(RegistrationEvents.ACCEPT, registrationAcceptedEvent.getRegistration().token, false);
    }


    @Async
    @EventListener
    @Transactional
    public void on(RegistrationCreatedEvent registrationCreatedEvent) {

        sendEventToStateMachine(RegistrationEvents.CREATE, registrationCreatedEvent.getRegistration().token, true);
    }


    private Message<RegistrationEvents> createEventMessage(RegistrationEvents event, String registrationToken) {

        return MessageBuilder.withPayload(event).setHeader("registration_token", registrationToken).build();
    }


    private String createMachineIdFromToken(String machineId) {

        return String.format(STATE_MACHINE_ID_PREFIX, machineId);
    }


    private void sendEventToStateMachine(RegistrationEvents event, String token, boolean startMachine) {

        StateMachine<RegistrationStates, RegistrationEvents> registrationStateMachine =
            stateMachineService.acquireStateMachine(createMachineIdFromToken(token), startMachine);

        registrationStateMachine.sendEvent(createEventMessage(event, token));
    }
}
