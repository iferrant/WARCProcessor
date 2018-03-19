package com.warcgenerator.core.rest;

import com.warcgenerator.core.rest.models.Token;
import com.warcgenerator.core.rest.models.User;
import org.glassfish.jersey.client.ClientProperties;
import wiremock.com.jayway.jsonpath.spi.impl.JacksonProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class ServerRequestService {
    public static final String API_URL = "http://localhost:8080/api/";
    public static final String LOGIN = "login";

    private Client client = ClientBuilder
            .newClient()
            .property(ClientProperties.CONNECT_TIMEOUT, 1000)
            .property(ClientProperties.READ_TIMEOUT, 1000)
            .register(JacksonProvider.class);

    /**
     * Request a new token based on the user credentials
     *
     * @param user User to login
     * @return Valid token
     */
    public Token loginUser(User user) {
        return client
                .target(API_URL)
                .path(LOGIN)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON))
                .readEntity(Token.class);
    }
}
