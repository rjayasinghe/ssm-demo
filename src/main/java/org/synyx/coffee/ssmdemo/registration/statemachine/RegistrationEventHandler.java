package org.synyx.coffee.ssmdemo.registration.statemachine;

import org.springframework.context.event.EventListener;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import org.springframework.scheduling.annotation.Async;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.registration.Registration;
import org.synyx.coffee.ssmdemo.registration.RegistrationRepository;
import org.synyx.coffee.ssmdemo.registration.events.RegistrationAcceptedEvent;
import org.synyx.coffee.ssmdemo.registration.events.RegistrationCreatedEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RegistrationEventHandler implements Loggable {

    private static final String STATE_MACHINE_ID_PREFIX = "registration_%s";
    private Map<String, String> machineIds = new ConcurrentHashMap<>();
    private final StateMachineService<RegistrationStates, RegistrationEvents> stateMachineService;
    private final RegistrationRepository registrationRepository;

    public RegistrationEventHandler(StateMachineService<RegistrationStates, RegistrationEvents> stateMachineService,
        RegistrationRepository registrationRepository) {

        this.stateMachineService = stateMachineService;
        this.registrationRepository = registrationRepository;
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

        registerMachineIdForToken(registrationCreatedEvent.getRegistration().token);
        sendEventToStateMachine(RegistrationEvents.CREATE, registrationCreatedEvent.getRegistration().token, true);
    }


    private void registerMachineIdForToken(final String token) {

        final String machineId = createMachineIdFromToken(token);
        machineIds.put(machineId, machineId);
    }


    private Message<RegistrationEvents> createEventMessage(RegistrationEvents event, String registrationToken) {

        return MessageBuilder.withPayload(event).setHeader("registration_token", registrationToken).build();
    }


    private String createMachineIdFromToken(String machineId) {

        return String.format(STATE_MACHINE_ID_PREFIX, machineId);
    }


    private void sendEventToStateMachine(RegistrationEvents event, String token, boolean startMachine) {

        final String machineId = createMachineIdFromToken(token);
        StateMachine<RegistrationStates, RegistrationEvents> registrationStateMachine =
            stateMachineService.acquireStateMachine(machineId);
        registrationStateMachine.sendEvent(createEventMessage(event, token));
    }


//    @Scheduled(fixedDelay = 2000L)
    @Transactional
    public void terminateFinishedRegistrations() {

        List<Registration> registrations = registrationRepository.findAll();

        registrations.forEach(registration -> {
            final String token = registration.getToken();
            final String machineId = createMachineIdFromToken(token);

            StateMachine<RegistrationStates, RegistrationEvents> registrationStateMachine =
                stateMachineService.acquireStateMachine(machineId);
            registrationStateMachine.sendEvent(createEventMessage(RegistrationEvents.TERMINATION_TIMEOUT, token));
        });
    }
}
