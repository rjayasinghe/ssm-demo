package org.synyx.coffee.ssmdemo.registration.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;


@Configuration
public class RegistrationStateMachineRuntimeConfig {

    @Bean
    public StateMachinePersister<RegistrationStates, RegistrationEvents, String> stateMachinePersister(
        StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister) {

        return new DefaultStateMachinePersister<>(stateMachineRuntimePersister);
    }


    @Bean
    public StateMachineService<RegistrationStates, RegistrationEvents> stateMachineService(
        StateMachineFactory<RegistrationStates, RegistrationEvents> stateMachineFactory,
        StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister) {

        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }


    @Bean
    public StateMachineRuntimePersister<RegistrationStates, RegistrationEvents, String> stateMachineRuntimePersister(
        JpaStateMachineRepository jpaStateMachineRepository) {

        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }
}
