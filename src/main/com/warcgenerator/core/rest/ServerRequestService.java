package com.warcgenerator.core.rest;

import com.warcgenerator.AppWarc;
import com.warcgenerator.core.rest.models.Token;
import com.warcgenerator.core.rest.models.User;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import wiremock.com.jayway.jsonpath.spi.impl.JacksonProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.File;

public class ServerRequestService {
    public static final String API_URL = "http://localhost:8080/api/";
    public static final String LOGIN = "login";
    public static final String CORPUS = "corpus";

    private Client client = ClientBuilder
            .newClient()
            .property(ClientProperties.CONNECT_TIMEOUT, 1000)
            .property(ClientProperties.READ_TIMEOUT, 1000);

    /**
     * Request a new token based on the user credentials
     *
     * @param user User to login
     * @return Valid token
     */
    public Token loginUser(User user) {
        return client
                .register(JacksonProvider.class)
                .target(API_URL)
                .path(LOGIN)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON))
                .readEntity(Token.class);
    }

    /**
     * Upload new corpus to the API
     */
    public void postCorpus(String corpusName, String corpusPath) {
        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File(corpusPath));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        final FormDataMultiPart multipart =
                (FormDataMultiPart) formDataMultiPart
                        .field("name", corpusName)
                        .bodyPart(filePart);
        String status = client.register(MultiPartFeature.class)
                .target(API_URL)
                .path(CORPUS)
                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + AppWarc.userGlobal.getToken().getToken())
                .post(Entity.entity(multipart, multipart.getMediaType()))
        .getStatusInfo().toString();
        System.out.println("HTTP status response: " +status);
    }
}
