package org.synyx.coffee.ssmdemo.notification.events;

public class PublishNotConfirmedEvent {

    private final String messageId;

    public PublishNotConfirmedEvent(String messageId) {

        this.messageId = messageId;
    }
}
