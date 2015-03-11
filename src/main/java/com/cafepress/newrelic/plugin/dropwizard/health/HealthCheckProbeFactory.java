package com.cafepress.newrelic.plugin.dropwizard.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class HealthCheckProbeFactory {

    private ObjectMapper objectMapper;

    public HealthCheckProbeFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HttpHealthCheckProbe build(String host, int port) {
        WebTarget adminWebTarget = ClientBuilder
                .newBuilder()
                .register(new JacksonJsonProvider(new ObjectMapper()))
                .build()
                .target("http://" + host + ":" + port);

        return new HttpHealthCheckProbe(adminWebTarget, objectMapper);
    }
}
