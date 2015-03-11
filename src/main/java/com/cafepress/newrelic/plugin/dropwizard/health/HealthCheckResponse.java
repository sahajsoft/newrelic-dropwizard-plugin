package com.cafepress.newrelic.plugin.dropwizard.health;

public class HealthCheckResponse {
    private final static int NO_HTTP_CODE = -1;

    public static HealthCheckResponse error() {
        return new HealthCheckResponse(NO_HTTP_CODE, HealthCheckResult.empty());
    }

    public static HealthCheckResponse from(int httpCode, HealthCheckResult result) {
        return new HealthCheckResponse(httpCode, result);
    }

    private int httpCode;
    private HealthCheckResult result;

    private HealthCheckResponse(int httpCode, HealthCheckResult result) {
        this.httpCode = httpCode;
        this.result = result;
    }

    public boolean isHealthy() {
        return httpCode == 200;
    }

    public boolean serverResponded() {
        return httpCode != NO_HTTP_CODE;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public HealthCheckResult getResult() {
        return result;
    }
}
