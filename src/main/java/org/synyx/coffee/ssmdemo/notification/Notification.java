package org.synyx.coffee.ssmdemo.notification;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "notification")
public class Notification {

    @Id
    private String token;

    public Notification() {
    }


    public Notification(String token) {

        this.token = token;
    }

    public String getToken() {

        return token;
    }


    public void setToken(String token) {

        this.token = token;
    }
}
