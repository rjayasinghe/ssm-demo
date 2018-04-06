package org.synyx.coffee.ssmdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.AdviceMode;

import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.statemachine.config.EnableStateMachine;


@SpringBootApplication
@EnableStateMachine
@EnableAsync(mode = AdviceMode.PROXY, proxyTargetClass = true)
public class SsmDemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(SsmDemoApplication.class, args);
    }
}
