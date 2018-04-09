package org.synyx.coffee.ssmdemo.registration.statemachine.actions;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.notification.events.ConfirmationCreateEvent;
import org.synyx.coffee.ssmdemo.registration.Registration;
import org.synyx.coffee.ssmdemo.registration.RegistrationRepository;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationEvents;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationStates;


@Component
public class CreatedAction implements Action<RegistrationStates, RegistrationEvents>, Loggable {

    private final RegistrationRepository registrationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CreatedAction(RegistrationRepository registrationRepository,
        ApplicationEventPublisher applicationEventPublisher) {

        this.registrationRepository = registrationRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public void execute(StateContext<RegistrationStates, RegistrationEvents> context) {

        final String token = StringUtils.hasText((String) context.getMessageHeader("registration_token"))
            ? (String) context.getMessageHeader("registration_token") : "no_token";
        Registration newRegistration = new Registration(token);
        registrationRepository.save(newRegistration);
        applicationEventPublisher.publishEvent(new ConfirmationCreateEvent(token));

        logger().info(String.format("created registration for %s and token %s", context.getStateMachine().getId(),
                token));
    }
}
