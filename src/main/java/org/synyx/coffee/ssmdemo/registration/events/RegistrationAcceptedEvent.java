package org.synyx.coffee.ssmdemo.registration.events;

import org.synyx.coffee.ssmdemo.registration.web.RegistrationDto;


public class RegistrationAcceptedEvent {

    private final RegistrationDto registration;

    public RegistrationAcceptedEvent(RegistrationDto registration) {

        this.registration = registration;
    }

    public RegistrationDto getRegistration() {

        return registration;
    }
}
