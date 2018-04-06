package org.synyx.coffee.ssmdemo.registration.web;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.stereotype.Service;

import org.synyx.coffee.ssmdemo.registration.events.RegistrationAcceptedEvent;
import org.synyx.coffee.ssmdemo.registration.events.RegistrationCreatedEvent;


@Service
public class RegistrationService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public RegistrationService(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void startRegistration(final RegistrationDto registrationDto) {

        applicationEventPublisher.publishEvent(new RegistrationCreatedEvent(registrationDto));
    }


    public void acceptRegistration(RegistrationDto registrationDto) {

        applicationEventPublisher.publishEvent(new RegistrationAcceptedEvent(registrationDto));
    }
}
