package org.synyx.coffee.ssmdemo.registration.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import org.synyx.coffee.ssmdemo.Loggable;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.AcceptedAction;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.CreatedAction;
import org.synyx.coffee.ssmdemo.registration.statemachine.actions.RejectedAction;

import java.util.EnumSet;


/**
 * Configuration for registration statemachines.
 *
 * @author  jayasinghe@synyx.de
 */
@Configuration
public class RegistrationStateMachineConfig implements Loggable {

    @Bean
    public StateMachinePersister<RegistrationStates, RegistrationEvents, String> stateMachinePersister(
        StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister) {

        return new DefaultStateMachinePersister<>(stateMachineRuntimePersister);
    }


    @Bean
    public StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister(
        JpaStateMachineRepository jpaStateMachineRepository) {

        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }


    @Bean
    public StateMachineBuilder.Builder<RegistrationStates, RegistrationEvents> registrationStateMachineBuilder(
        StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister,
        AcceptedAction acceptedAction, RejectedAction rejectedAction, CreatedAction createdAction) throws Exception {

        StateMachineBuilder.Builder<RegistrationStates, RegistrationEvents> builder = StateMachineBuilder
                .<RegistrationStates, RegistrationEvents>builder();

        builder.configureConfiguration().withConfiguration().autoStartup(true);
        builder.configureConfiguration().withPersistence().runtimePersister(stateMachineRuntimePersister);

        builder.configureStates()
            .withStates()
            .initial(RegistrationStates.INITIAL)
            .states(EnumSet.allOf(RegistrationStates.class));

        builder.configureTransitions()
            .withExternal()
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
            .action(acceptedAction)
            .and()
            .withExternal()
            .source(RegistrationStates.NEW)
            .target(RegistrationStates.REJECTED)
            .event(RegistrationEvents.REJECT)
            .action(rejectedAction)
            .and()
            .withExternal()
            .source(RegistrationStates.PRE_ACCEPTED)
            .target(RegistrationStates.ACCEPTED)
            .event(RegistrationEvents.CREATE)
            .action(acceptedAction)
            .and()
            .withExternal()
            .source(RegistrationStates.PRE_REJECTED)
            .target(RegistrationStates.REJECTED)
            .event(RegistrationEvents.CREATE)
            .action(rejectedAction)
            .and()
            .withExternal()
            .source(RegistrationStates.NEW)
            .target(RegistrationStates.CONFIRMED)
            .event(RegistrationEvents.CONFIRM)
            .and()
            .withExternal()
            .source(RegistrationStates.ACCEPTED)
            .target(RegistrationStates.TERMINAL)
            .event(RegistrationEvents.ACCEPT_TIMEOUT)
            .and()
            .withExternal()
            .source(RegistrationStates.REJECTED)
            .target(RegistrationStates.TERMINAL)
            .event(RegistrationEvents.REJECT_TIMEOUT);

        return builder;
    }
}
