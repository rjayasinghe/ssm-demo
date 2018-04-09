package org.synyx.coffee.ssmdemo.notification.events;

public class PublishConfirmedEvent {

    private final String messageId;

    public PublishConfirmedEvent(final String messageId) {

        this.messageId = messageId;
    }

    public String getMessageId() {

        return messageId;
    }
}
