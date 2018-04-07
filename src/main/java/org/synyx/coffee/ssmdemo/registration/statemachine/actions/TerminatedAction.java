package org.synyx.coffee.ssmdemo.registration.statemachine.actions;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.registration.RegistrationRepository;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationEvents;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationStates;


@Component
public class TerminatedAction implements Action<RegistrationStates, RegistrationEvents>, Loggable {

    private final RegistrationRepository registrationRepository;

    public TerminatedAction(RegistrationRepository registrationRepository) {

        this.registrationRepository = registrationRepository;
    }

    @Override
    @Transactional
    public void execute(StateContext<RegistrationStates, RegistrationEvents> context) {

        if (context.getMessage().getHeaders().containsKey("registration_token")) {
            final String token = (String) context.getMessage().getHeaders().get("registration_token");
            registrationRepository.delete(token);
            logger().info("deleted registration for token {}", token);
        } else {
            logger().warn("encountered termination without token");
        }
    }
}
