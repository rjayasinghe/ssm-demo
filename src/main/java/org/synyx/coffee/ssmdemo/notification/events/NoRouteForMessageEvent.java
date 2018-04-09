package org.synyx.coffee.ssmdemo.notification.events;

public class NoRouteForMessageEvent {

    private final String messageId;

    public NoRouteForMessageEvent(String messageId) {

        this.messageId = messageId;
    }

    public String getMessageId() {

        return messageId;
    }
}
