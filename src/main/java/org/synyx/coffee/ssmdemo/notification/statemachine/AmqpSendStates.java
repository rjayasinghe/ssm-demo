package org.synyx.coffee.ssmdemo.notification.statemachine;

public enum AmqpSendStates {

    INITIAL,
    PUBLISH_CONFIRMED,
    PREPARED,
    NEW,
    RELAYED,
    FAILED
}
