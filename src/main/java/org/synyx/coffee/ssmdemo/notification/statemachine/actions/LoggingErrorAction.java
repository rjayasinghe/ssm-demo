package org.synyx.coffee.ssmdemo.notification.statemachine.actions;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendEvents;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendStates;


@Component
public class LoggingErrorAction implements Action<AmqpSendStates, AmqpSendEvents>, Loggable {

    @Override
    public void execute(StateContext<AmqpSendStates, AmqpSendEvents> context) {

        logger().warn("got exception during state transition: ", context.getException());
    }
}
