package com.cafepress.newrelic.plugin.dropwizard;

import com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckProbe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DropwizardAgentTest {
    private static final String AGENT_NAME = "agent-name";

    @Mock private HealthCheckProbe healthCheckProbe;
    private DropwizardAgent agent;

    @Before
    public void setUp() throws Exception {
        agent = new DropwizardAgent(AGENT_NAME, healthCheckProbe);
    }

    @Test
    public void shouldReportAgentNameCorrectly() throws Exception {
        assertThat(agent.getAgentName(), is(AGENT_NAME));
    }
}