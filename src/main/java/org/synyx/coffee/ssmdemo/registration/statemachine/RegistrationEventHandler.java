package org.synyx.coffee.ssmdemo.registration.statemachine;

import org.springframework.context.event.EventListener;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import org.springframework.scheduling.annotation.Async;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.registration.events.RegistrationAcceptedEvent;
import org.synyx.coffee.ssmdemo.registration.events.RegistrationCreatedEvent;


@Component
public class RegistrationEventHandler {

    private static final String STATE_MACHINE_ID_PREFIX = "registration_%s";
    private final StateMachineBuilder.Builder<RegistrationStates, RegistrationEvents> stateMachineBuilder;

    public RegistrationEventHandler(
        StateMachineBuilder.Builder<RegistrationStates, RegistrationEvents> stateMachineBuilder) {

        this.stateMachineBuilder = stateMachineBuilder;
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

        configureMachineIdOnBuilder(createMachineIdFromToken(token));

        StateMachine<RegistrationStates, RegistrationEvents> registrationStateMachine = stateMachineBuilder.build();

        if (startMachine) {
            registrationStateMachine.start();
        }

        registrationStateMachine.sendEvent(createEventMessage(event, token));
    }


    private void configureMachineIdOnBuilder(String machineId) {

        try {
            stateMachineBuilder.configureConfiguration().withConfiguration().machineId(machineId);
        } catch (Exception e) {
            throw new RuntimeException("failed to configure machineId for confirmationDeliveryStateMachine");
        }
    }
}
