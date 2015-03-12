package com.cafepress.newrelic.plugin.dropwizard;

import com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckProbe;
import com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckResponse;
import com.google.common.base.Strings;
import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.util.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.jcabi.manifests.Manifests.exists;
import static com.jcabi.manifests.Manifests.read;

public class DropwizardAgent extends Agent {
    private static final Logger logger = Logger.getLogger(DropwizardAgent.class);

    private static final String GUID = "com.cafepress.newrelic.plugin.dropwizard";
    private static final String MANFIEST_VERSION_NAME = "New-Relic-Plugin-Version";
    private static final String VERSION = exists(MANFIEST_VERSION_NAME) ? read(MANFIEST_VERSION_NAME) : "dev-version";

    private String agentName;
    private HealthCheckProbe healthCheckProbe;

    public DropwizardAgent(String agentName, HealthCheckProbe healthCheckProbe) {
        super(GUID, VERSION);
        checkArgument(!Strings.isNullOrEmpty(agentName));
        this.agentName = agentName;
        this.healthCheckProbe = healthCheckProbe;
        logger.info("Starting dropwizard agent version ", VERSION, " for ", healthCheckProbe);
    }

    @Override
    public void pollCycle() {
        HealthCheckResponse healthCheckResponse = healthCheckProbe.probe();
        reportHealthCheckMetrics(healthCheckResponse);
    }

    private void reportHealthCheckMetrics(HealthCheckResponse response) {
        // provides the overall metric that is alerted on
        reportMetric("Health/Unhealthy/Overall", "bool", asInt(!response.isHealthy()));

        // provides visualization that responses are (or are not) being received
        reportMetric("Health/HttpCode/" + (response.serverResponded() ? response.getHttpCode() : "NoResponse"), "bool", asInt(true));

        // provides visualization of which health check(s) are failing
        for (String check : response.getResult().healthCheckNames()) {
            reportMetric("Health/Unhealthy/Individual/" + check, "bool", asInt(!response.getResult().isHealthy(check)));
        }

        // for cases where we have no data (service is down or /healthcheck has no json for some reason), this
        // adds a data point within the failing health checks so it's obvious why the overall check is failing
        // when no individual check appears to be failing
        if (response.getResult().healthCheckNames().isEmpty()) {
            reportMetric("Health/Unhealthy/Individual/NoData", "bool", asInt(true));
        }
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    private int asInt(boolean value) {
        return value ? 1 : 0;
    }
}
