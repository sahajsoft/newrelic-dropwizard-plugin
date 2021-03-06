package com.cafepress.newrelic.plugin.dropwizard.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.newrelic.metrics.publish.util.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpHealthCheckProbe implements HealthCheckProbe {
    private static final Logger logger = Logger.getLogger(HttpHealthCheckProbe.class);

    private final WebTarget healthCheckResource;
    private final ObjectMapper objectMapper;

    public HttpHealthCheckProbe(WebTarget adminWebTarget, ObjectMapper objectMapper) {
        checkNotNull(adminWebTarget);
        checkNotNull(objectMapper);
        this.healthCheckResource = adminWebTarget.path("/healthcheck");
        this.objectMapper = objectMapper;
    }

    @Override
    public HealthCheckResponse probe() {
        Response webResponse = null;
        try {
            webResponse = healthCheckResource.request(MediaType.APPLICATION_JSON_TYPE).get();
            return parseHealthCheck(webResponse);
        } catch (Exception e) {
            logger.error(e, "Unable to get response from ", healthCheckResource.getUri());
            return HealthCheckResponse.error();
        } finally {
            close(webResponse);
        }
    }

    private HealthCheckResponse parseHealthCheck(Response response) {
        HealthCheckResult healthCheckResult;
        try {
            String json = response.readEntity(String.class);
            if (Strings.isNullOrEmpty(json)) {
                healthCheckResult = HealthCheckResult.empty();
            } else {
                healthCheckResult = objectMapper.readValue(json, HealthCheckResult.class);
            }
        } catch (Exception e) {
            logger.error(e, "Unable to parse health check json");
            healthCheckResult = HealthCheckResult.empty();
        }

        return HealthCheckResponse.from(response.getStatus(), healthCheckResult);
    }

    private void close(Response webResponse) {
        if (webResponse == null) {
            return;
        }

        try {
            webResponse.close();
        } catch (RuntimeException e) {
            logger.error(e, "Problem closing response");
        }
    }

    @Override
    public String toString() {
        return "HttpHealthCheckProbe{" + "healthCheckResource=" + healthCheckResource + '}';
    }
}
