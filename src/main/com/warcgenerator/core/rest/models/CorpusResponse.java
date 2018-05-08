package com.warcgenerator.core.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CorpusResponse {
    private String name;
    private String uuid;
    @JsonProperty("user_uuid")
    private String userUuid;
    private String path;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("created_at")
    private String createdAt;

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getPath() {
        return path;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
