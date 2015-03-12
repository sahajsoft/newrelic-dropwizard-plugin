package com.cafepress.newrelic.plugin.dropwizard;

import com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckProbeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class Main {
    public static void main(String[] args) {
        try {
            Runner runner = new Runner();
            HealthCheckProbeFactory healthCheckProbeFactory = new HealthCheckProbeFactory(new ObjectMapper());
            DropwizardAgentFactory agentFactory = new DropwizardAgentFactory(healthCheckProbeFactory);
            runner.add(agentFactory);
            runner.setupAndRun(); // Never returns
        } catch (ConfigurationException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }
}
