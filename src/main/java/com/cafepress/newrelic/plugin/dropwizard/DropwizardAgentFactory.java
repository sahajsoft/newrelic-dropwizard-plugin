package com.cafepress.newrelic.plugin.dropwizard;

import com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckProbe;
import com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckProbeFactory;
import com.google.common.base.Strings;
import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

import java.util.Map;

public class DropwizardAgentFactory extends AgentFactory {

    private HealthCheckProbeFactory healthCheckProbeFactory;

    public DropwizardAgentFactory(HealthCheckProbeFactory healthCheckProbeFactory) {
        this.healthCheckProbeFactory = healthCheckProbeFactory;
    }

    @Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
        String host = readHost(properties);
        String adminPath = readAdminPath(properties);
        int port = readAdminPort(properties);
        String name = readName(properties, host);

        HealthCheckProbe healthCheckProbe = healthCheckProbeFactory.build(host, port, adminPath);
        return new DropwizardAgent(name, healthCheckProbe);
    }

    private String readAdminPath(Map<String, Object> properties) {
        return Strings.nullToEmpty((String) properties.get("adminPath"));
    }

    private String readName(Map<String, Object> properties, String defaultName) {
        String name = (String) properties.get("name");
        return Strings.isNullOrEmpty(name) ? defaultName : name;
    }

    private String readHost(Map<String, Object> properties) {
        return (String) properties.get("host");
    }

    private int readAdminPort(Map<String, Object> properties) {
        if (properties.containsKey("adminPort")) {
            return ((Number) properties.get("adminPort")).intValue();
        } else {
            // default Dropwizard admin port
            return 8081;
        }
    }
}
