package org.synyx.coffee.ssmdemo.registration;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "registration")
public class Registration {

    @Id
    private String token;

    public Registration() {
    }


    public Registration(String token) {

        this.token = token;
    }

    public String getToken() {

        return token;
    }


    public void setToken(String token) {

        this.token = token;
    }
}
