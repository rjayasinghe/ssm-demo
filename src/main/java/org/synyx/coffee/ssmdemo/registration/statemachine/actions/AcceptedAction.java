package org.synyx.coffee.ssmdemo.registration.statemachine.actions;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationEvents;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationStates;


@Component
public class AcceptedAction implements Action<RegistrationStates, RegistrationEvents>, Loggable {

    @Override
    public void execute(StateContext<RegistrationStates, RegistrationEvents> context) {

        final String token = StringUtils.hasText((String) context.getMessageHeader("registration_token"))
            ? (String) context.getMessageHeader("registration_token") : "no_token";

        logger().info(String.format("accepted registration for %s and token %s", context.getStateMachine().getId(),
                token));
    }
}
