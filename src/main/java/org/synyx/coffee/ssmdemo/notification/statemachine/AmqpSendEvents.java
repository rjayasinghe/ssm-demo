package org.synyx.coffee.ssmdemo.notification.statemachine;

public enum AmqpSendEvents {

    PUBLISH_CONFIRMED,
    NOT_PUBLISHED,
    NOT_ROUTED,
    RE_SEND,
    START,
    TIMEOUT
}
