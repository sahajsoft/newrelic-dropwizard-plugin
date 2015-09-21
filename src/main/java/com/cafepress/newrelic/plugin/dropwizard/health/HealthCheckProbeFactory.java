package com.cafepress.newrelic.plugin.dropwizard.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Strings;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class HealthCheckProbeFactory {

    private ObjectMapper objectMapper;

    public HealthCheckProbeFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HttpHealthCheckProbe build(String host, Integer port, String adminContext) {
        String url = "http://" + host ;
        if(port!=null) {
            url += ":" + port;
        }
        if(!Strings.isNullOrEmpty(adminContext)) {
            url += "/" + adminContext;
        }
        WebTarget adminWebTarget = ClientBuilder
                .newBuilder()
                .register(new JacksonJsonProvider(new ObjectMapper()))
                .build()
                .target(url);

        return new HttpHealthCheckProbe(adminWebTarget, objectMapper);
    }
}
