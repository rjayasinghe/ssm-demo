package org.synyx.coffee.ssmdemo.notification.statemachine;

import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.notification.statemachine.actions.LoggingErrorAction;
import org.synyx.coffee.ssmdemo.notification.statemachine.actions.NotificationPersistAction;
import org.synyx.coffee.ssmdemo.notification.statemachine.actions.RoutedAction;
import org.synyx.coffee.ssmdemo.notification.statemachine.actions.SendAction;

import java.util.EnumSet;


/**
 * Configuration for registration statemachines.
 *
 * @author  jayasinghe@synyx.de
 */
@Configuration
@EnableStateMachineFactory(name = "amqpSendStateMachineFactory")
public class AmqpSendStateMachineConfig extends EnumStateMachineConfigurerAdapter<AmqpSendStates, AmqpSendEvents>
    implements Loggable {

    private final StateMachineRuntimePersister<AmqpSendStates, AmqpSendEvents, String> stateMachineRuntimePersister;
    private final RoutedAction routedAction;
    private final NotificationPersistAction notificationPersistAction;
    private final LoggingErrorAction loggingErrorAction;
    private final SendAction sendAction;

    public AmqpSendStateMachineConfig(
        StateMachineRuntimePersister<AmqpSendStates, AmqpSendEvents, String> amqpSendStateMachineRuntimePersister,
        RoutedAction routedAction, NotificationPersistAction notificationPersistAction,
        LoggingErrorAction loggingErrorAction, SendAction sendAction) {

        this.stateMachineRuntimePersister = amqpSendStateMachineRuntimePersister;
        this.routedAction = routedAction;
        this.notificationPersistAction = notificationPersistAction;
        this.loggingErrorAction = loggingErrorAction;
        this.sendAction = sendAction;
    }

    @Override
    public void configure(StateMachineStateConfigurer<AmqpSendStates, AmqpSendEvents> states) throws Exception {

        states.withStates()
            .initial(AmqpSendStates.INITIAL)
            .states(EnumSet.allOf(AmqpSendStates.class))
            .end(AmqpSendStates.ROUTED);
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<AmqpSendStates, AmqpSendEvents> transitions)
        throws Exception {

        transitions.withExternal()
            .source(AmqpSendStates.INITIAL)
            .target(AmqpSendStates.PREPARED)
            .event(AmqpSendEvents.SEND)
            .action(sendAction, loggingErrorAction)
            .action(notificationPersistAction)
            .and()
            .withExternal()
            .source(AmqpSendStates.PREPARED)
            .target(AmqpSendStates.ROUTED)
            .event(AmqpSendEvents.PUBLISH_CONFIRMED)
            .action(routedAction, loggingErrorAction)
            .and()
            .withExternal()
            .source(AmqpSendStates.INITIAL)
            .target(AmqpSendStates.ROUTED)
            .event(AmqpSendEvents.PUBLISH_CONFIRMED)
            .action(routedAction, loggingErrorAction)
            .and()
            .withExternal()
            .source(AmqpSendStates.PREPARED)
            .target(AmqpSendStates.FAILED)
            .event(AmqpSendEvents.NOT_PUBLISHED)
            .and()
            .withExternal()
            .source(AmqpSendStates.PREPARED)
            .target(AmqpSendStates.FAILED)
            .event(AmqpSendEvents.NOT_ROUTED)
            .and()
            .withExternal()
            .source(AmqpSendStates.PREPARED)
            .target(AmqpSendStates.FAILED)
            .timer(5000L)
            .and()
            .withExternal()
            .source(AmqpSendStates.FAILED)
            .target(AmqpSendStates.INITIAL)
            .timer(2000L);
    }


    @Override
    public void configure(StateMachineConfigurationConfigurer<AmqpSendStates, AmqpSendEvents> config)
        throws Exception {

        super.configure(config);
        config.withConfiguration().autoStartup(true);
        config.withPersistence().runtimePersister(stateMachineRuntimePersister);
    }
}
