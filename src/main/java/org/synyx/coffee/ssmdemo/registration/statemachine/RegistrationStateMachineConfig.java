package org.synyx.coffee.ssmdemo.registration.statemachine;

import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.AcceptedAction;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.CreatedAction;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.RejectedAction;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.TerminatedAction;

import java.util.EnumSet;


/**
 * Configuration for registration statemachines.
 *
 * @author  jayasinghe@synyx.de
 */
@Configuration
@EnableStateMachineFactory
public class RegistrationStateMachineConfig
    extends EnumStateMachineConfigurerAdapter<RegistrationStates, RegistrationEvents> implements Loggable {

    private final StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister;
    private final CreatedAction createdAction;
    private final TerminatedAction terminatedAction;

    public RegistrationStateMachineConfig(
        StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister,
        CreatedAction createdAction, TerminatedAction terminatedAction) {

        this.stateMachineRuntimePersister = stateMachineRuntimePersister;
        this.createdAction = createdAction;
        this.terminatedAction = terminatedAction;
    }

    @Override
    public void configure(StateMachineStateConfigurer<RegistrationStates, RegistrationEvents> states)
        throws Exception {

        states.withStates().initial(RegistrationStates.INITIAL).states(EnumSet.allOf(RegistrationStates.class));
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<RegistrationStates, RegistrationEvents> transitions)
        throws Exception {

        transitions.withExternal()
            .source(RegistrationStates.INITIAL)
            .target(RegistrationStates.NEW)
            .event(RegistrationEvents.CREATE)
            .action(createdAction)
            .and()
            .withExternal()
            .source(RegistrationStates.INITIAL)
            .target(RegistrationStates.PRE_REJECTED)
            .event(RegistrationEvents.REJECT)
            .and()
            .withExternal()
            .source(RegistrationStates.INITIAL)
            .target(RegistrationStates.PRE_ACCEPTED)
            .event(RegistrationEvents.ACCEPT)
            .and()
            .withExternal()
            .source(RegistrationStates.NEW)
            .target(RegistrationStates.ACCEPTED)
            .event(RegistrationEvents.ACCEPT)
            .action(new AcceptedAction())
            .and()
            .withExternal()
            .source(RegistrationStates.NEW)
            .target(RegistrationStates.REJECTED)
            .event(RegistrationEvents.REJECT)
            .action(new RejectedAction())
            .and()
            .withExternal()
            .source(RegistrationStates.PRE_ACCEPTED)
            .target(RegistrationStates.ACCEPTED)
            .event(RegistrationEvents.CREATE)
            .action(new AcceptedAction())
            .and()
            .withExternal()
            .source(RegistrationStates.PRE_REJECTED)
            .target(RegistrationStates.REJECTED)
            .event(RegistrationEvents.CREATE)
            .action(new RejectedAction())
            .and()
            .withExternal()
            .source(RegistrationStates.NEW)
            .target(RegistrationStates.CONFIRMED)
            .event(RegistrationEvents.CONFIRM)
            .and()
            .withExternal()
            .source(RegistrationStates.ACCEPTED)
            .target(RegistrationStates.TERMINAL)
            .event(RegistrationEvents.TERMINATION_TIMEOUT)
            .action(terminatedAction)
            .and()
            .withExternal()
            .source(RegistrationStates.REJECTED)
            .target(RegistrationStates.TERMINAL)
            .event(RegistrationEvents.TERMINATION_TIMEOUT)
            .action(terminatedAction);
    }


    @Override
    public void configure(StateMachineConfigurationConfigurer<RegistrationStates, RegistrationEvents> config)
        throws Exception {

        super.configure(config);
        config.withConfiguration().autoStartup(true);
        config.withPersistence().runtimePersister(stateMachineRuntimePersister);
    }
}
