package com.example.wolframnotebooks;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Interaction {
    private final String id;
    private final String endpoint;
    private final String requestPayload;
    private final String responsePayload;
    private final Instant timestamp;
    private final long responseTimeMillis;
    private final String user;
    private final Map<String, String> metadata;

    public Interaction(String endpoint, String requestPayload, String responsePayload, long responseTimeMillis, String user, Map<String, String> metadata) {
        this.id = UUID.randomUUID().toString();
        this.endpoint = endpoint;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
        this.timestamp = Instant.now();
        this.responseTimeMillis = responseTimeMillis;
        this.user = user;
        this.metadata = metadata;
    }

    public String getId() { return id; }
    public String getEndpoint() { return endpoint; }
    public String getRequestPayload() { return requestPayload; }
    public String getResponsePayload() { return responsePayload; }
    public Instant getTimestamp() { return timestamp; }
    public long getResponseTimeMillis() { return responseTimeMillis; }
    public String getUser() { return user; }
    public Map<String, String> getMetadata() { return metadata; }
} 