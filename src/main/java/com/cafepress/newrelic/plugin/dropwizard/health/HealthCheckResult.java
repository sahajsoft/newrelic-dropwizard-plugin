package com.cafepress.newrelic.plugin.dropwizard.health;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Represents the json response of a Dropwizard /healthcheck admin request.
 * <p/>
 * An individual check can be healthy or unhealthy and can optional include errors and messages:
 * <pre> {@code
 * {
 *   "deadlocks" : {
 *     "healthy" : true
 *   },
 *   "database" : {
 *     "healthy" : true,
 *     "message" : "connection pool healthy"
 *   },
 *   "cache" : {
 *     "healthy" : false,
 *     "message" : "actively evicting"
 *   },
 *   "anotherDatabase" : {
 *     "healthy" : false,
 *     "message" : "Login failed for user",
 *     "error" : {
 *       "message" : "Login failed for user 'unknown'.",
 *       "stack" : [ "net.sourceforge.jtds.jdbc.SQLDiagnostic.addDiagnostic(SQLDiagnostic.java:372)",
 *                   "etc" ]
 *     }
 *   }
 * }
 * }</pre>
 */
@JsonDeserialize(using = HealthCheckResult.Deserializer.class)
public class HealthCheckResult {
    private static final HealthCheckResult NO_RESULT = new HealthCheckResult(Collections.<String, Status>emptyMap());

    public static HealthCheckResult empty() {
        return NO_RESULT;
    }

    private Map<String, Status> statuses;

    private HealthCheckResult(Map<String, Status> statuses) {
        this.statuses = statuses;
    }

    public Set<String> healthCheckNames() {
        return statuses.keySet();
    }

    public boolean isHealthy(String name) {
        return statuses.get(name).isHealthy();
    }

    @Override
    public String toString() {
        return "HealthCheckResult{" +
                statuses +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Status {
        private boolean healthy;

        public boolean isHealthy() {
            return healthy;
        }
    }

    static class Deserializer extends StdDeserializer<HealthCheckResult> {
        public Deserializer() {
            super(HealthCheckResult.class);
        }

        @Override
        public HealthCheckResult deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            Map<String, Status> statuses = parser.readValueAs(new TypeReference<Map<String, Status>>() {
            });
            return new HealthCheckResult(statuses);
        }
    }
}
