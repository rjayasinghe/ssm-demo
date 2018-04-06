package org.synyx.coffee.ssmdemo.registration.statemachine;

public enum RegistrationStates {

    INITIAL,
    NEW,
    PRE_REJECTED,
    REJECTED,
    PRE_ACCEPTED,
    ACCEPTED,
    CONFIRMED,
    TERMINAL
}
