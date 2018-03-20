package com.warcgenerator.core.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representation class that defines a User object used to retrieve a valid token.
 * This object would be saved on memory to store the user credentials, but
 * it wouldn't be saved in any configuration file for security reasons.
 *
 * @JsonIgnoreProperties annotation is used to mark a property
 * or a list of properties to be ignored at the class level.
 * With true value, ignore all properties unknown
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String email;
    private String password;
    private Token token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
