package org.synyx.coffee.ssmdemo.notification.statemachine;

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
public class AmqpSendStateMachineRuntimeConfig {

    @Bean
    public StateMachinePersister<AmqpSendStates, AmqpSendEvents, String> amqpSendStateMachinePersister(
        StateMachineRuntimePersister<AmqpSendStates, AmqpSendEvents, String> amqpSendStateMachineRuntimePersister) {

        return new DefaultStateMachinePersister<>(amqpSendStateMachineRuntimePersister);
    }


    @Bean
    public StateMachineService<AmqpSendStates, AmqpSendEvents> amqpSendStateMachineService(
        StateMachineFactory<AmqpSendStates, AmqpSendEvents> amqpSendStateMachineFactory,
        StateMachineRuntimePersister<AmqpSendStates, AmqpSendEvents, String> amqpSendStateMachineRuntimePersister) {

        return new DefaultStateMachineService<>(amqpSendStateMachineFactory, amqpSendStateMachineRuntimePersister);
    }


    @Bean
    public StateMachineRuntimePersister<AmqpSendStates, AmqpSendEvents, String> amqpSendStateMachineRuntimePersister(
        JpaStateMachineRepository jpaStateMachineRepository) {

        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }
}
