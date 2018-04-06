package org.synyx.coffee.ssmdemo.registration.events;

import org.synyx.coffee.ssmdemo.registration.web.RegistrationDto;


public class RegistrationCreatedEvent {

    private final RegistrationDto registration;

    public RegistrationCreatedEvent(RegistrationDto registration) {

        this.registration = registration;
    }

    public RegistrationDto getRegistration() {

        return registration;
    }
}
