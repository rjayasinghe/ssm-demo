package org.synyx.coffee.ssmdemo.notification.statemachine.actions;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendEvents;
import org.synyx.coffee.ssmdemo.notification.statemachine.AmqpSendStates;


@Component
public class SendAction implements Action<AmqpSendStates, AmqpSendEvents> {

    private static final String CONFIRMATION_ROUTING_KEY = "confirmation.created";
    private final RabbitTemplate rabbitTemplate;

    public SendAction(RabbitTemplate rabbitTemplate) {

        this.rabbitTemplate = rabbitTemplate;
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

        CorrelationData correlationData = new CorrelationData(token);
        rabbitTemplate.send("notifications", CONFIRMATION_ROUTING_KEY,
            MessageBuilder.withBody(token.getBytes()).setMessageId(token).build(), correlationData);
    }
}
