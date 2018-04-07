package org.synyx.coffee.ssmdemo.registration.statemachine.actions;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationEvents;
import org.synyx.coffee.ssmdemo.registration.statemachine.RegistrationStates;


@Component
public class TerminatedAction implements Action<RegistrationStates, RegistrationEvents> {

    @Override
    public void execute(StateContext<RegistrationStates, RegistrationEvents> context) {
    }
}
