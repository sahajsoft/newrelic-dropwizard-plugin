package com.cafepress.newrelic.plugin.dropwizard.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HealthCheckResultTest {

    @Test
    public void shouldDeserializeHealthyCorrectly() throws Exception {
        HealthCheckResult result = fromFixture("fixtures/healthy.json");
        assertThat(result.healthCheckNames(), contains("deadlocks", "database", "cache", "anotherDatabase"));
        assertThat(result.isHealthy("deadlocks"), is(true));
        assertThat(result.isHealthy("database"), is(true));
        assertThat(result.isHealthy("cache"), is(true));
        assertThat(result.isHealthy("anotherDatabase"), is(true));
    }

    @Test
    public void shouldDeserializeUnhealthyCorrectly() throws Exception {
        HealthCheckResult result = fromFixture("fixtures/unhealthy.json");
        assertThat(result.healthCheckNames(), contains("deadlocks", "database", "cache", "anotherDatabase"));
        assertThat(result.isHealthy("deadlocks"), is(true));
        assertThat(result.isHealthy("database"), is(true));
        assertThat(result.isHealthy("cache"), is(false));
        assertThat(result.isHealthy("anotherDatabase"), is(false));
    }

    private HealthCheckResult fromFixture(String path) throws IOException {
        String json = Resources.toString(Resources.getResource(path), Charsets.UTF_8);
        return new ObjectMapper().readValue(json, HealthCheckResult.class);
    }
}