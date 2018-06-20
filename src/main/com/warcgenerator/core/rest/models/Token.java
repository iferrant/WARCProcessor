package com.warcgenerator.core.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representation class that defines a Token object
 *
 * @JsonIgnoreProperties annotation is used to mark a property
 * or a list of properties to be ignored at the class level.
 * With true value, ignore all properties unknown
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
    private String token;

    public String getToken() {
        return token;
    }
}
