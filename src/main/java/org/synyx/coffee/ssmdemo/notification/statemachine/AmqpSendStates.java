package org.synyx.coffee.ssmdemo.notification.statemachine;

public enum AmqpSendStates {

    INITIAL,
    ROUTED,
    PREPARED,
    FAILED
}
