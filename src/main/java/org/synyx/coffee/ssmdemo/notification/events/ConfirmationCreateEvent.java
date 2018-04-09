package org.synyx.coffee.ssmdemo.notification.events;

public class ConfirmationCreateEvent {

    private final String token;

    public ConfirmationCreateEvent(String token) {

        this.token = token;
    }

    public String getToken() {

        return token;
    }
}
