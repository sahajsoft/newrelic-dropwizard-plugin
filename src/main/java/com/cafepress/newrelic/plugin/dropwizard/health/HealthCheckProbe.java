package com.cafepress.newrelic.plugin.dropwizard.health;

public interface HealthCheckProbe {
    public HealthCheckResponse probe();
}
