package com.warcgenerator.core.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representation class that defines a User object used to retrieve a valid token
 *
 * @JsonIgnoreProperties annotation is used to mark a property
 * or a list of properties to be ignored at the class level.
 * With true value, ignore all properties unknown
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
